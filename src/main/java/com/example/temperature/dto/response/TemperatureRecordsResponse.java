package com.example.temperature.dto.response;

import java.util.List;

public record TemperatureRecordsResponse(
        List<TemperatureRecordResponse> records,
        int page,
        int limit,
        int count
) {
}
