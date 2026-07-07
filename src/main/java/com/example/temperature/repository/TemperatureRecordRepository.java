package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, Long> {

    @Query("""
            select record
            from TemperatureRecordEntity record
            where (:startTime is null or record.readingTimestamp >= :startTime)
              and (:endTime is null or record.readingTimestamp <= :endTime)
            """)
    List<TemperatureRecordEntity> findByOptionalTimeRange(
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime,
            Pageable pageable
    );
}
