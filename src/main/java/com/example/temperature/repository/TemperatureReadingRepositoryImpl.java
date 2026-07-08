package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureReadingEntity;
import com.example.temperature.validation.SortOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class TemperatureReadingRepositoryImpl implements TemperatureReadingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void insertAll(List<TemperatureReadingEntity> readings) {
        for (TemperatureReadingEntity reading : readings) {
            entityManager.createNativeQuery("""
                            INSERT INTO temperature_readings (reading_timestamp, temperature_value)
                            VALUES (:readingTimestamp, :temperatureValue)
                            """)
                    .setParameter("readingTimestamp", reading.getReadingTimestamp())
                    .setParameter("temperatureValue", reading.getTemperatureValue())
                    .executeUpdate();
        }
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
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT reading_timestamp, temperature_value
                        FROM temperature_readings
                        WHERE reading_timestamp >= :startTime
                          AND reading_timestamp <= :endTime
                        ORDER BY reading_timestamp %s
                        LIMIT :limit OFFSET :offset
                        """.formatted(direction))
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setParameter("limit", limit)
                .setParameter("offset", offset)
                .getResultList();

        return rows.stream()
                .map(row -> new TemperatureReadingEntity(toOffsetDateTime(row[0]), (BigDecimal) row[1]))
                .toList();
    }

    @Override
    public long countByTimestampRange(OffsetDateTime startTime, OffsetDateTime endTime) {
        Number count = (Number) entityManager.createNativeQuery("""
                        SELECT COUNT(*)
                        FROM temperature_readings
                        WHERE reading_timestamp >= :startTime
                          AND reading_timestamp <= :endTime
                        """)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getSingleResult();
        return count.longValue();
    }

    private OffsetDateTime toOffsetDateTime(Object value) {
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().atOffset(ZoneOffset.UTC);
        }
        throw new IllegalStateException("Unsupported timestamp value type: " + value.getClass().getName());
    }
}
