package com.example.temperature.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureRecordResponse(
        OffsetDateTime timestamp,
        BigDecimal temperature
) {
}
