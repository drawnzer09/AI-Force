package com.example.temperature.api.dto;

import com.example.temperature.validation.FiniteBigDecimal;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureDataPointRequest(
        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "temperature is required")
        @FiniteBigDecimal(message = "temperature must be a finite JSON number")
        BigDecimal temperature
) {
}
