package com.example.temperature.dto.response;

import java.time.OffsetDateTime;

public record TemperatureRecordResponse(
        OffsetDateTime timestamp,
        Double temperature
) {
}
