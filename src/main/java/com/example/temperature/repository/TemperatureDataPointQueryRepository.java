package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureDataPointEntity;

import java.time.OffsetDateTime;
import java.util.List;

public interface TemperatureDataPointQueryRepository {

    List<TemperatureDataPointEntity> findByTimestampRange(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp,
            int limit,
            int offset
    );
}
