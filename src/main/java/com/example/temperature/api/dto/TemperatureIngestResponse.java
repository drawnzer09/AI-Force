package com.example.temperature.api.dto;

import java.time.OffsetDateTime;

public record TemperatureIngestResponse(
        int recordsStored,
        OffsetDateTime earliestTimestamp,
        OffsetDateTime latestTimestamp
) {
}
