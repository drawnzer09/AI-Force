package com.example.temperature.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureDataPointDto(
        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "temperatureValue is required")
        BigDecimal temperatureValue
) {
}
