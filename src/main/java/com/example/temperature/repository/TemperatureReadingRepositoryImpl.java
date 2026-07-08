package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureReadingEntity;
import com.example.temperature.validation.SortOrder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class TemperatureReadingRepositoryImpl implements TemperatureReadingRepository {

    private static final RowMapper<TemperatureReadingEntity> ROW_MAPPER = (rs, rowNum) -> {
        Timestamp timestamp = rs.getTimestamp("reading_timestamp");
        return new TemperatureReadingEntity(
                timestamp.toInstant().atOffset(ZoneOffset.UTC),
                rs.getBigDecimal("temperature_value")
        );
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TemperatureReadingRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertAll(List<TemperatureReadingEntity> readings) {
        String sql = """
                INSERT INTO temperature_readings (reading_timestamp, temperature_value)
                VALUES (:readingTimestamp, :temperatureValue)
                """;

        MapSqlParameterSource[] batch = readings.stream()
                .map(reading -> new MapSqlParameterSource()
                        .addValue("readingTimestamp", reading.getReadingTimestamp())
                        .addValue("temperatureValue", reading.getTemperatureValue()))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batch);
    }

    @Override
    public List<TemperatureReadingEntity> findByTimestampRange(
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            int limit,
            int offset,
            SortOrder sortOrder
    ) {
        String direction = sortOrder == SortOrder.TIMESTAMP_DESC ? "DESC" : "ASC";
        String sql = """
                SELECT reading_timestamp, temperature_value
                FROM temperature_readings
                WHERE reading_timestamp >= :startTime
                  AND reading_timestamp <= :endTime
                ORDER BY reading_timestamp %s
                LIMIT :limit OFFSET :offset
                """.formatted(direction);

        return jdbcTemplate.query(sql, parameters(startTime, endTime)
                .addValue("limit", limit)
                .addValue("offset", offset), ROW_MAPPER);
    }

    @Override
    public long countByTimestampRange(OffsetDateTime startTime, OffsetDateTime endTime) {
        String sql = """
                SELECT COUNT(*)
                FROM temperature_readings
                WHERE reading_timestamp >= :startTime
                  AND reading_timestamp <= :endTime
                """;
        Long count = jdbcTemplate.queryForObject(sql, parameters(startTime, endTime), Long.class);
        return count == null ? 0L : count;
    }

    private MapSqlParameterSource parameters(OffsetDateTime startTime, OffsetDateTime endTime) {
        return new MapSqlParameterSource()
                .addValue("startTime", startTime)
                .addValue("endTime", endTime);
    }
}
