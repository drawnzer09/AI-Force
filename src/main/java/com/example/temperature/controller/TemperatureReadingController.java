package com.example.temperature.controller;

import com.example.temperature.dto.request.IngestTemperatureReadingsRequest;
import com.example.temperature.dto.request.TemperatureReadingQueryRequest;
import com.example.temperature.dto.response.IngestTemperatureReadingsResponse;
import com.example.temperature.dto.response.TemperatureReadingQueryResponse;
import com.example.temperature.service.TemperatureReadingService;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/temperature-readings", produces = MediaType.APPLICATION_JSON_VALUE)
public class TemperatureReadingController {

    private final TemperatureReadingService service;

    public TemperatureReadingController(TemperatureReadingService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngestTemperatureReadingsResponse> ingest(@Valid @RequestBody IngestTemperatureReadingsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ingest(request));
    }

    @GetMapping
    public ResponseEntity<TemperatureReadingQueryResponse> query(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(name = "sourceId", required = false) String sourceId,
            @RequestParam(name = "unit", required = false) String unit,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        TemperatureReadingQueryRequest request = new TemperatureReadingQueryRequest(from, to, sourceId, unit, limit, offset, sort);
        return ResponseEntity.ok(service.query(request));
    }
}
