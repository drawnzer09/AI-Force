package com.example.temperature.dto.response;

import java.util.List;

public record TemperatureQueryResponse(
        List<TemperatureRecordResponse> data,
        PaginationResponse pagination
) {
}
