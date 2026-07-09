package com.example.temperature.service;

import com.example.temperature.dto.request.IngestTemperatureReadingsRequest;
import com.example.temperature.dto.request.TemperatureReadingQueryRequest;
import com.example.temperature.dto.response.IngestTemperatureReadingsResponse;
import com.example.temperature.dto.response.TemperatureReadingQueryResponse;

public interface TemperatureReadingService {

    IngestTemperatureReadingsResponse ingest(IngestTemperatureReadingsRequest request);

    TemperatureReadingQueryResponse query(TemperatureReadingQueryRequest request);
}
