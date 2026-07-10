package com.example.temperature.controller;

import com.example.temperature.dto.request.IngestTemperatureRecordsRequest;
import com.example.temperature.dto.response.IngestTemperatureRecordsResponse;
import com.example.temperature.dto.response.TemperatureRecordsResponse;
import com.example.temperature.service.TemperatureRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@Validated
@RestController
@RequestMapping("/v1/temperature-records")
public class TemperatureRecordController {

    private final TemperatureRecordService service;

    public TemperatureRecordController(TemperatureRecordService service) {
        this.service = service;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public IngestTemperatureRecordsResponse ingest(@Valid @RequestBody IngestTemperatureRecordsRequest request) {
        return service.ingest(request);
    }

    @GetMapping(produces = "application/json")
    public TemperatureRecordsResponse query(
            @RequestParam @NotNull(message = "startTime is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @NotNull(message = "endTime is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        return service.query(startTime, endTime, page, limit);
    }
}
