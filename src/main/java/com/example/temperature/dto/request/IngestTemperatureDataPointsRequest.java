package com.example.temperature.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record IngestTemperatureDataPointsRequest(
        @NotNull(message = "data is required")
        @Size(min = 1, max = 1000, message = "data must contain between 1 and 1000 items")
        List<@Valid TemperatureDataPointRequest> data
) {
}
