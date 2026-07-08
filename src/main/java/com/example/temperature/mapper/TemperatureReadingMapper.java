package com.example.temperature.mapper;

import com.example.temperature.dto.temperature.TemperatureReadingRequestDto;
import com.example.temperature.dto.temperature.TemperatureReadingResponseDto;
import com.example.temperature.persistence.TemperatureReadingRow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TemperatureReadingMapper {

    public TemperatureReadingRow toRow(TemperatureReadingRequestDto dto) {
        return new TemperatureReadingRow(dto.timestamp(), dto.temperature());
    }

    public TemperatureReadingResponseDto toResponseDto(TemperatureReadingRow row) {
        return new TemperatureReadingResponseDto(row.timestamp(), row.temperature());
    }

    public List<TemperatureReadingRow> toRows(List<TemperatureReadingRequestDto> dtos) {
        return dtos.stream().map(this::toRow).toList();
    }

    public List<TemperatureReadingResponseDto> toResponseDtos(List<TemperatureReadingRow> rows) {
        return rows.stream().map(this::toResponseDto).toList();
    }
}
