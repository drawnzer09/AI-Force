package com.example.temperature.dto.request;

import com.example.temperature.validation.FiniteBigDecimal;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureRecordRequest(
        @NotNull(message = "timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "temperatureValue is required")
        @FiniteBigDecimal
        BigDecimal temperatureValue
) {
}
