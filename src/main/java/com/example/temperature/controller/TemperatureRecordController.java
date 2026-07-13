package com.example.temperature.controller;

import com.example.temperature.dto.request.IngestTemperatureRecordsRequest;
import com.example.temperature.dto.response.IngestTemperatureRecordsResponse;
import com.example.temperature.dto.response.TemperatureRecordPageResponse;
import com.example.temperature.service.TemperatureRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

@Validated
@RestController
@RequestMapping(path = "/v1/temperature-records", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemperatureRecordController {

    private final TemperatureRecordService service;

    public TemperatureRecordController(TemperatureRecordService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IngestTemperatureRecordsResponse ingest(
            @Valid @RequestBody IngestTemperatureRecordsRequest request
    ) {
        return service.ingest(request);
    }

    @GetMapping
    public TemperatureRecordPageResponse query(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime to,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page must be greater than or equal to 0")
            int page,

            @RequestParam(defaultValue = "100")
            @Min(value = 1, message = "size must be between 1 and 1000")
            @Max(value = 1000, message = "size must be between 1 and 1000")
            int size,

            @RequestParam(defaultValue = "asc")
            @Pattern(regexp = "asc|desc", message = "sort must be either asc or desc")
            String sort
    ) {
        return service.query(from, to, page, size, sort);
    }
}
