package com.example.temperature.mapper;

import com.example.temperature.api.dto.TemperatureRecordRequest;
import com.example.temperature.api.dto.TemperatureRecordResponse;
import com.example.temperature.persistence.entity.TemperatureMeasurementEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class TemperatureMeasurementMapper {

    public TemperatureMeasurementEntity toEntity(TemperatureRecordRequest request) {
        return new TemperatureMeasurementEntity(request.timestamp().toInstant(), request.temperature());
    }

    public TemperatureRecordResponse toResponse(TemperatureMeasurementEntity entity) {
        return new TemperatureRecordResponse(
                OffsetDateTime.ofInstant(entity.getMeasurementTimestamp(), ZoneOffset.UTC),
                entity.getTemperature()
        );
    }
}
