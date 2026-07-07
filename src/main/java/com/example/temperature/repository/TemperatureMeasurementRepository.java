package com.example.temperature.repository;

import com.example.temperature.persistence.entity.TemperatureMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TemperatureMeasurementRepository extends JpaRepository<TemperatureMeasurementEntity, Long> {

    @Query(
            value = """
                    SELECT temperature_measurement_id, measurement_timestamp, temperature
                    FROM temperature_measurements
                    WHERE measurement_timestamp >= :startTimestamp
                      AND measurement_timestamp <= :endTimestamp
                    ORDER BY measurement_timestamp ASC, temperature_measurement_id ASC
                    LIMIT :limit OFFSET :offset
                    """,
            nativeQuery = true
    )
    List<TemperatureMeasurementEntity> findByTimestampRange(
            @Param("startTimestamp") Instant startTimestamp,
            @Param("endTimestamp") Instant endTimestamp,
            @Param("limit") int limit,
            @Param("offset") long offset
    );
}
