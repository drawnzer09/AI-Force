package com.example.temperature.mapper;

import com.example.temperature.api.dto.TemperatureDataPointRequest;
import com.example.temperature.api.dto.TemperatureDataPointResponse;
import com.example.temperature.persistence.entity.TemperatureDataPointEntity;
import org.springframework.stereotype.Component;

@Component
public class TemperatureDataPointMapper {

    public TemperatureDataPointEntity toEntity(TemperatureDataPointRequest request) {
        return new TemperatureDataPointEntity(request.timestamp(), request.temperature());
    }

    public TemperatureDataPointResponse toResponse(TemperatureDataPointEntity entity) {
        return new TemperatureDataPointResponse(entity.getRecordedAt(), entity.getTemperature());
    }
}
