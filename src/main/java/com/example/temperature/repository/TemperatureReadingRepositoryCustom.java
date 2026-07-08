package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureReadingEntity;
import com.example.temperature.validation.SortOrder;

import java.time.OffsetDateTime;
import java.util.List;

public interface TemperatureReadingRepositoryCustom {

    void insertAll(List<TemperatureReadingEntity> readings);

    List<TemperatureReadingEntity> findByTimestampRange(
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            int limit,
            int offset,
            SortOrder sortOrder
    );

    long countByTimestampRange(OffsetDateTime startTime, OffsetDateTime endTime);
}
