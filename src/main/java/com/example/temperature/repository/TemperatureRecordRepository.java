package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, Long> {

    @Query("""
            select record
            from TemperatureRecordEntity record
            where (:startTime is null or record.recordedAt >= :startTime)
              and (:endTime is null or record.recordedAt <= :endTime)
            """)
    Page<TemperatureRecordEntity> findByOptionalRecordedAtRange(
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime,
            Pageable pageable
    );
}
