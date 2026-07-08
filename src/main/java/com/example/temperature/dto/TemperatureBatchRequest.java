package com.example.temperature.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TemperatureBatchRequest(
        @NotEmpty(message = "dataPoints must contain at least one item")
        @Size(max = 1000, message = "dataPoints must contain no more than 1000 items")
        List<@Valid TemperatureDataPointDto> dataPoints
) {
}
