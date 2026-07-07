package com.example.temperature.api.dto;

import java.util.List;

public record TemperatureQueryResponse(
        List<TemperatureRecordResponse> records,
        PaginationResponse pagination
) {
}
