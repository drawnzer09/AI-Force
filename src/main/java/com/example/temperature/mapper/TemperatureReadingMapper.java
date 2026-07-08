package com.example.temperature.mapper;

import com.example.temperature.dto.TemperatureDataPointDto;
import com.example.temperature.entity.TemperatureReadingEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TemperatureReadingMapper {

    public TemperatureReadingEntity toEntity(TemperatureDataPointDto dto) {
        return new TemperatureReadingEntity(dto.timestamp(), dto.temperatureValue());
    }

    public TemperatureDataPointDto toDto(TemperatureReadingEntity entity) {
        return new TemperatureDataPointDto(entity.getReadingTimestamp(), entity.getTemperatureValue());
    }

    public List<TemperatureReadingEntity> toEntities(List<TemperatureDataPointDto> dataPoints) {
        return dataPoints.stream().map(this::toEntity).toList();
    }

    public List<TemperatureDataPointDto> toDtos(List<TemperatureReadingEntity> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}
