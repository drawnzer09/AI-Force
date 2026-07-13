package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, Long> {

    Page<TemperatureRecordEntity> findByRecordedAtBetween(
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    );

    Page<TemperatureRecordEntity> findByRecordedAtGreaterThanEqual(
            OffsetDateTime from,
            Pageable pageable
    );

    Page<TemperatureRecordEntity> findByRecordedAtLessThanEqual(
            OffsetDateTime to,
            Pageable pageable
    );
}
