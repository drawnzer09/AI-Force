package com.example.temperature.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureRecordRequest(
        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "temperature is required")
        BigDecimal temperature
) {
}
