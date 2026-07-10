package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, Long> {

    @Query("""
            select record
            from TemperatureRecordEntity record
            where (:from is null or record.measurementTimestamp >= :from)
              and (:to is null or record.measurementTimestamp <= :to)
            """)
    Page<TemperatureRecordEntity> findByOptionalMeasurementTimestampRange(
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable
    );
}
