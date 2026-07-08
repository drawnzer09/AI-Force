package com.example.temperature.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record TemperatureQueryRequest(
        @NotNull(message = "startTime is required")
        OffsetDateTime startTime,

        @NotNull(message = "endTime is required")
        OffsetDateTime endTime,

        @Min(value = 1, message = "limit must be at least 1")
        @Max(value = 1000, message = "limit must be no more than 1000")
        Integer limit,

        @Min(value = 0, message = "offset must be at least 0")
        Integer offset,

        String sort
) {
}
