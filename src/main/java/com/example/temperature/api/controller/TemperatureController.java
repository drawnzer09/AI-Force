package com.example.temperature.api.controller;

import com.example.temperature.api.dto.TemperatureIngestRequest;
import com.example.temperature.api.dto.TemperatureIngestResponse;
import com.example.temperature.api.dto.TemperatureQueryResponse;
import com.example.temperature.api.validation.QueryRangeValidator;
import com.example.temperature.service.TemperatureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(path = "/api/v1/temperatures", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TemperatureController {

    private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);

    private final TemperatureService temperatureService;
    private final QueryRangeValidator queryRangeValidator;

    public TemperatureController(TemperatureService temperatureService, QueryRangeValidator queryRangeValidator) {
        this.temperatureService = temperatureService;
        this.queryRangeValidator = queryRangeValidator;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TemperatureIngestResponse ingest(@Valid @RequestBody TemperatureIngestRequest request) {
        TemperatureIngestResponse response = temperatureService.ingest(request);
        log.info("Temperature ingestion request completed with {} records stored", response.recordsStored());
        return response;
    }

    @GetMapping
    public TemperatureQueryResponse query(
            @RequestParam
            @NotNull(message = "startTimestamp is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime startTimestamp,

            @RequestParam
            @NotNull(message = "endTimestamp is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime endTimestamp,

            @RequestParam(defaultValue = "1000")
            @Min(value = 1, message = "limit must be between 1 and 10000")
            @Max(value = 10000, message = "limit must be between 1 and 10000")
            int limit,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "offset must be greater than or equal to 0")
            long offset
    ) {
        queryRangeValidator.validate(startTimestamp, endTimestamp);
        return temperatureService.query(startTimestamp, endTimestamp, limit, offset);
    }
}
