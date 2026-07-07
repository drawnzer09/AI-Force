package com.example.temperature.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TemperatureIngestRequest(
        @NotNull(message = "records is required")
        @Size(min = 1, max = 1000, message = "records must contain between 1 and 1000 items")
        List<@Valid TemperatureRecordRequest> records
) {
}
