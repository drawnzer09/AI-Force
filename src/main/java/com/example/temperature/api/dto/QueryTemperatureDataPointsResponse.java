package com.example.temperature.api.dto;

import java.util.List;

public record QueryTemperatureDataPointsResponse(
        List<TemperatureDataPointResponse> dataPoints,
        PageMetadataResponse page
) {
}
