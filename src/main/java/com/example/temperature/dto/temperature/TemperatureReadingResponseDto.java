package com.example.temperature.dto.temperature;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureReadingResponseDto(
        OffsetDateTime timestamp,
        BigDecimal temperature
) {
}
