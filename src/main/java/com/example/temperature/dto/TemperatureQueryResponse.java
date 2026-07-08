package com.example.temperature.dto;

import java.util.List;

public record TemperatureQueryResponse(
        List<TemperatureDataPointDto> dataPoints,
        PageMetadataResponse page
) {
}
