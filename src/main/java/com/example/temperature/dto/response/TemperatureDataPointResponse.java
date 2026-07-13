package com.example.temperature.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureDataPointResponse(
        OffsetDateTime timestamp,
        BigDecimal temperature
) {
}
