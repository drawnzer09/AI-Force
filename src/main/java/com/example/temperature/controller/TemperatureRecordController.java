package com.example.temperature.controller;

import com.example.temperature.dto.request.TemperatureBatchRequest;
import com.example.temperature.dto.response.TemperatureBatchResponse;
import com.example.temperature.dto.response.TemperatureQueryResponse;
import com.example.temperature.service.TemperatureRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(path = "/v1/temperature-records", produces = "application/json")
public class TemperatureRecordController {

    private final TemperatureRecordService service;

    public TemperatureRecordController(TemperatureRecordService service) {
        this.service = service;
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TemperatureBatchResponse ingest(@Valid @RequestBody TemperatureBatchRequest request) {
        return service.ingest(request);
    }

    @GetMapping
    public TemperatureQueryResponse query(
            @org.springframework.web.bind.annotation.RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime from,
            @org.springframework.web.bind.annotation.RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime to,
            @org.springframework.web.bind.annotation.RequestParam(required = false)
            Integer page,
            @org.springframework.web.bind.annotation.RequestParam(required = false)
            Integer pageSize,
            @org.springframework.web.bind.annotation.RequestParam(required = false)
            String sort
    ) {
        return service.query(from, to, page, pageSize, sort);
    }
}
