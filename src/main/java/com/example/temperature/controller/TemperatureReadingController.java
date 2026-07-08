package com.example.temperature.controller;

import com.example.temperature.dto.temperature.IngestTemperatureReadingsRequestDto;
import com.example.temperature.dto.temperature.IngestTemperatureReadingsResponseDto;
import com.example.temperature.dto.temperature.TemperatureReadingsResponseDto;
import com.example.temperature.service.TemperatureReadingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping(path = "/v1/temperature-readings", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TemperatureReadingController {

    private final TemperatureReadingService service;

    public TemperatureReadingController(TemperatureReadingService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IngestTemperatureReadingsResponseDto ingest(
            @Valid @RequestBody IngestTemperatureReadingsRequestDto request) {
        return service.ingest(request);
    }

    @GetMapping
    public TemperatureReadingsResponseDto query(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime fromTimestamp,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime toTimestamp,

            @RequestParam(required = false)
            @Min(value = 1, message = "limit must be greater than or equal to 1")
            @Max(value = 1000, message = "limit must be less than or equal to 1000")
            Integer limit,

            @RequestParam(required = false)
            @Min(value = 0, message = "offset must be greater than or equal to 0")
            Integer offset) {
        return service.query(fromTimestamp, toTimestamp, limit, offset);
    }
}
