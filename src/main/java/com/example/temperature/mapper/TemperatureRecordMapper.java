package com.example.temperature.mapper;

import com.example.temperature.dto.request.TemperatureRecordRequest;
import com.example.temperature.dto.response.TemperatureRecordResponse;
import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.stereotype.Component;

@Component
public class TemperatureRecordMapper {

    public TemperatureRecordEntity toEntity(TemperatureRecordRequest request) {
        return new TemperatureRecordEntity(request.getTimestamp(), request.getTemperature());
    }

    public TemperatureRecordResponse toResponse(TemperatureRecordEntity entity) {
        return new TemperatureRecordResponse(entity.getId(), entity.getTimestamp(), entity.getTemperature());
    }
}
