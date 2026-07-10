package com.example.temperature.controller;

import com.example.temperature.dto.request.TemperatureBatchRequest;
import com.example.temperature.dto.response.TemperatureBatchResponse;
import com.example.temperature.dto.response.TemperatureRecordQueryResponse;
import com.example.temperature.service.TemperatureRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(path = "/api/v1/temperature-records", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemperatureRecordController {

    private final TemperatureRecordService service;

    public TemperatureRecordController(TemperatureRecordService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemperatureBatchResponse> ingest(@Valid @RequestBody TemperatureBatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ingest(request));
    }

    @GetMapping
    public TemperatureRecordQueryResponse query(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime startTime,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime endTime,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page must be greater than or equal to 0")
            int page,

            @RequestParam(required = false)
            @Min(value = 1, message = "pageSize must be between 1 and 1000")
            @Max(value = 1000, message = "pageSize must be between 1 and 1000")
            Integer pageSize
    ) {
        int appliedPageSize = pageSize == null ? service.defaultPageSize() : pageSize;
        return service.query(startTime, endTime, page, appliedPageSize);
    }
}
