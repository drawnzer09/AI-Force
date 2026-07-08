package com.example.temperature.repository;

import com.example.temperature.persistence.TemperatureReadingRow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TemperatureReadingRepositoryImpl implements TemperatureReadingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int saveAll(List<TemperatureReadingRow> rows) {
        int inserted = 0;
        for (TemperatureReadingRow row : rows) {
            inserted += entityManager.createNativeQuery("""
                            INSERT INTO temperature_readings (reading_timestamp, temperature)
                            VALUES (:readingTimestamp, :temperature)
                            """)
                    .setParameter("readingTimestamp", row.timestamp())
                    .setParameter("temperature", row.temperature())
                    .executeUpdate();
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
        if (fromTimestamp != null) {
            sql.append(" AND reading_timestamp >= :fromTimestamp");
        }
        if (toTimestamp != null) {
            sql.append(" AND reading_timestamp <= :toTimestamp");
        }
        sql.append(" ORDER BY reading_timestamp ASC LIMIT :limit OFFSET :offset");

        Query query = entityManager.createNativeQuery(sql.toString());
        if (fromTimestamp != null) {
            query.setParameter("fromTimestamp", fromTimestamp);
        }
        if (toTimestamp != null) {
            query.setParameter("toTimestamp", toTimestamp);
        }
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<TemperatureReadingRow> rows = new ArrayList<>(results.size());
        for (Object[] result : results) {
            rows.add(new TemperatureReadingRow(toOffsetDateTime(result[0]), (BigDecimal) result[1]));
        }
        return rows;
    }

    @Override
    public boolean isAvailable() {
        try {
            Object result = entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return result != null;
        } catch (RuntimeException ex) {
            throw new DataAccessResourceFailureException("PostgreSQL availability check failed", ex);
        }
    }

    private OffsetDateTime toOffsetDateTime(Object value) {
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        if (value instanceof Instant instant) {
            return instant.atOffset(ZoneOffset.UTC);
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().atOffset(ZoneOffset.UTC);
        }
        throw new IllegalStateException("Unsupported timestamp value type: " + value.getClass().getName());
    }
}
