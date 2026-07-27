package com.example.temperature.api.controller;

import com.example.temperature.api.dto.IngestTemperatureDataPointsRequest;
import com.example.temperature.api.dto.IngestTemperatureDataPointsResponse;
import com.example.temperature.api.dto.QueryTemperatureDataPointsResponse;
import com.example.temperature.service.TemperatureDataPointService;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/temperature-data-points", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TemperatureDataPointController {

    private final TemperatureDataPointService service;

    public TemperatureDataPointController(TemperatureDataPointService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IngestTemperatureDataPointsResponse ingest(@Valid @RequestBody IngestTemperatureDataPointsRequest request) {
        return service.ingest(request);
    }

    @GetMapping
    public QueryTemperatureDataPointsResponse query(
            @RequestParam(required = false) OffsetDateTime fromTimestamp,
            @RequestParam(required = false) OffsetDateTime toTimestamp,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return service.query(fromTimestamp, toTimestamp, page, size);
    }
}
