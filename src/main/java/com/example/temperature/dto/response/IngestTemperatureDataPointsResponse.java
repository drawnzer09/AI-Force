package com.example.temperature.dto.response;

import java.util.List;

public record IngestTemperatureDataPointsResponse(
        int acceptedCount,
        List<TemperatureDataPointResponse> data
) {
}
