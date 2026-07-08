package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureReadingEntity;
import com.example.temperature.entity.TemperatureReadingKey;
import org.springframework.data.repository.Repository;

public interface TemperatureReadingRepository extends Repository<TemperatureReadingEntity, TemperatureReadingKey>, TemperatureReadingRepositoryCustom {
}
