package com.example.temperature.mapper;

import com.example.temperature.dto.request.TemperatureDataPointRequest;
import com.example.temperature.dto.response.TemperatureDataPointResponse;
import com.example.temperature.entity.TemperatureDataPointEntity;
import org.springframework.stereotype.Component;

@Component
public class TemperatureDataPointMapper {

    public TemperatureDataPointEntity toEntity(TemperatureDataPointRequest request) {
        return new TemperatureDataPointEntity(request.timestamp(), request.temperature());
    }

    public TemperatureDataPointResponse toResponse(TemperatureDataPointEntity entity) {
        return new TemperatureDataPointResponse(entity.getTimestamp(), entity.getTemperature());
    }
}
