package com.example.temperature.controller;

import com.example.temperature.dto.request.IngestTemperatureDataPointsRequest;
import com.example.temperature.dto.response.IngestTemperatureDataPointsResponse;
import com.example.temperature.dto.response.QueryTemperatureDataPointsResponse;
import com.example.temperature.service.TemperatureDataPointService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.OffsetDateTime;

@RestController
@RequestMapping(path = "/api/v1/temperature-data-points")
@Validated
public class TemperatureDataPointController {

    private final TemperatureDataPointService service;

    public TemperatureDataPointController(TemperatureDataPointService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IngestTemperatureDataPointsResponse ingest(
            @Valid @RequestBody IngestTemperatureDataPointsRequest request
    ) {
        return service.ingest(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public QueryTemperatureDataPointsResponse query(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime startTimestamp,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime endTimestamp,

            @RequestParam(required = false)
            Integer limit,

            @RequestParam(required = false)
            Integer offset
    ) {
        return service.query(startTimestamp, endTimestamp, limit, offset);
    }
}
