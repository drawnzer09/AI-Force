package com.example.temperature.dto.response;

import java.util.List;

public record TemperatureRecordPageResponse(
        List<TemperatureRecordResponse> records,
        PageMetadataResponse page
) {
}
