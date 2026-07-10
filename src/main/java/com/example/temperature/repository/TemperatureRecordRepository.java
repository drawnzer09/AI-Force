package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, Long> {

    Page<TemperatureRecordEntity> findByRecordedAtBetween(Instant startTime, Instant endTime, Pageable pageable);
}
