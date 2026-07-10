package com.example.temperature.dto.response;

import java.util.List;

public record TemperatureRecordQueryResponse(
        List<TemperatureRecordResponse> records,
        int page,
        int pageSize,
        int count,
        boolean hasMore
) {
}
