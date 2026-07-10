package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TemperatureRecordRepository extends JpaRepository<TemperatureRecordEntity, UUID>,
        JpaSpecificationExecutor<TemperatureRecordEntity> {
}
