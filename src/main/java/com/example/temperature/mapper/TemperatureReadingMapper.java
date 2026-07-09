package com.example.temperature.mapper;

import com.example.temperature.dto.request.TemperatureReadingRequest;
import com.example.temperature.dto.response.TemperatureReadingResponse;
import com.example.temperature.entity.TemperatureReadingEntity;
import org.springframework.stereotype.Component;

@Component
public class TemperatureReadingMapper {

    public TemperatureReadingEntity toEntity(TemperatureReadingRequest request) {
        TemperatureReadingEntity entity = new TemperatureReadingEntity();
        entity.setSourceId(request.getSourceId().trim());
        entity.setReadingTimestamp(request.getTimestamp());
        entity.setTemperature(request.getTemperature());
        entity.setUnit(request.getUnit());
        return entity;
    }

    public TemperatureReadingResponse toResponse(TemperatureReadingEntity entity) {
        return new TemperatureReadingResponse(
                entity.getSourceId(),
                entity.getReadingTimestamp(),
                entity.getTemperature(),
                entity.getUnit()
        );
    }
}
