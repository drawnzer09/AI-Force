package com.example.temperature.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureRecordResponse(
        OffsetDateTime timestamp,
        BigDecimal temperature
) {
}
