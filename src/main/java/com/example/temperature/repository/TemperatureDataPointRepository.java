package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureDataPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureDataPointRepository extends JpaRepository<TemperatureDataPointEntity, Long>, TemperatureDataPointQueryRepository {
}
