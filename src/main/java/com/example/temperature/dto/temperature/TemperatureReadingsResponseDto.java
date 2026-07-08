package com.example.temperature.dto.temperature;

import java.util.List;

public record TemperatureReadingsResponseDto(
        List<TemperatureReadingResponseDto> records,
        int count,
        int limit,
        int offset
) {
}
