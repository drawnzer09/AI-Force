package com.example.temperature.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureDataPointRequest(
        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "temperature is required")
        BigDecimal temperature
) {
}
