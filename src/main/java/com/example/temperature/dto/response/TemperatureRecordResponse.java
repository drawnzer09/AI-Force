package com.example.temperature.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TemperatureRecordResponse(
        Instant timestamp,
        BigDecimal temperature
) {
}
