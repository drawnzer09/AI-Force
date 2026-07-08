package com.example.temperature.repository;

import com.example.temperature.persistence.TemperatureReadingRow;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TemperatureReadingRepositoryImpl implements TemperatureReadingRepository {

    private static final RowMapper<TemperatureReadingRow> TEMPERATURE_READING_ROW_MAPPER = new TemperatureReadingRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public TemperatureReadingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int saveAll(List<TemperatureReadingRow> rows) {
        int inserted = 0;
        for (TemperatureReadingRow row : rows) {
            inserted += jdbcTemplate.update(
                    """
                    INSERT INTO temperature_readings (reading_timestamp, temperature)
                    VALUES (?, ?)
                    """,
                    row.timestamp(),
                    row.temperature()
            );
        }
        return inserted;
    }

    @Override
    public List<TemperatureReadingRow> findByTimestampRange(OffsetDateTime fromTimestamp,
                                                            OffsetDateTime toTimestamp,
                                                            int limit,
                                                            int offset) {
        StringBuilder sql = new StringBuilder("""
                SELECT reading_timestamp, temperature
                FROM temperature_readings
                WHERE 1 = 1
                """);
        List<Object> arguments = new ArrayList<>();
        List<Integer> argumentTypes = new ArrayList<>();

        if (fromTimestamp != null) {
            sql.append(" AND reading_timestamp >= ?");
            arguments.add(fromTimestamp);
            argumentTypes.add(Types.TIMESTAMP_WITH_TIMEZONE);
        }
        if (toTimestamp != null) {
            sql.append(" AND reading_timestamp <= ?");
            arguments.add(toTimestamp);
            argumentTypes.add(Types.TIMESTAMP_WITH_TIMEZONE);
        }

        sql.append(" ORDER BY reading_timestamp ASC LIMIT ? OFFSET ?");
        arguments.add(limit);
        argumentTypes.add(Types.INTEGER);
        arguments.add(offset);
        argumentTypes.add(Types.INTEGER);

        int[] types = argumentTypes.stream().mapToInt(Integer::intValue).toArray();
        return jdbcTemplate.query(sql.toString(), arguments.toArray(), types, TEMPERATURE_READING_ROW_MAPPER);
    }

    @Override
    public boolean isAvailable() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return result != null && result == 1;
        } catch (DataAccessException ex) {
            throw new DataAccessResourceFailureException("PostgreSQL availability check failed", ex);
        }
    }

    private static final class TemperatureReadingRowMapper implements RowMapper<TemperatureReadingRow> {

        @Override
        public TemperatureReadingRow mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            OffsetDateTime timestamp = toOffsetDateTime(resultSet.getObject("reading_timestamp"));
            return new TemperatureReadingRow(timestamp, resultSet.getBigDecimal("temperature"));
        }

        private static OffsetDateTime toOffsetDateTime(Object value) {
            if (value instanceof OffsetDateTime offsetDateTime) {
                return offsetDateTime;
            }
            if (value instanceof Timestamp timestamp) {
                return timestamp.toInstant().atOffset(ZoneOffset.UTC);
            }
            throw new SQLExceptionWrapper("Unsupported timestamp value type: " + value.getClass().getName());
        }
    }

    private static final class SQLExceptionWrapper extends RuntimeException {
        private SQLExceptionWrapper(String message) {
            super(message);
        }
    }
}
