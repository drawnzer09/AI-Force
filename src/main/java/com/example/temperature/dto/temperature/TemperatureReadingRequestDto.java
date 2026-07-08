package com.example.temperature.dto.temperature;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureReadingRequestDto(
        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "temperature is required")
        BigDecimal temperature
) {
}
