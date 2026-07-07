package com.example.temperature.dto.response;

import java.util.List;

public record TemperatureBatchResponse(
        int acceptedCount,
        List<TemperatureRecordResponse> records
) {
}
