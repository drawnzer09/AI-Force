package com.example.temperature.dto.response;

import java.util.List;

public record QueryTemperatureDataPointsResponse(
        List<TemperatureDataPointResponse> data,
        PaginationResponse pagination
) {
}
