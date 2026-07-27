package com.example.temperature.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureDataPointResponse(
        OffsetDateTime timestamp,
        BigDecimal temperature
) {
}
