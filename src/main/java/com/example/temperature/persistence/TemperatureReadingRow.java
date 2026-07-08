package com.example.temperature.persistence;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TemperatureReadingRow(
        OffsetDateTime timestamp,
        BigDecimal temperature
) {
}
