package com.example.temperature.api;

import com.example.temperature.dto.TemperatureBatchRequest;
import com.example.temperature.dto.TemperatureIngestResponse;
import com.example.temperature.dto.TemperatureQueryRequest;
import com.example.temperature.dto.TemperatureQueryResponse;
import com.example.temperature.service.TemperatureDataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/v1/temperature-data")
@Validated
public class TemperatureDataController {

    private static final Logger log = LoggerFactory.getLogger(TemperatureDataController.class);

    private final TemperatureDataService temperatureDataService;

    public TemperatureDataController(TemperatureDataService temperatureDataService) {
        this.temperatureDataService = temperatureDataService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemperatureIngestResponse ingest(@Valid @RequestBody TemperatureBatchRequest request) {
        log.info("Received temperature batch with {} data points", request.dataPoints().size());
        return temperatureDataService.ingest(request);
    }

    @GetMapping
    public TemperatureQueryResponse query(
            @RequestParam @NotNull(message = "startTime is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime startTime,

            @RequestParam @NotNull(message = "endTime is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime endTime,

            @RequestParam(required = false) @Min(value = 1, message = "limit must be at least 1")
            @Max(value = 1000, message = "limit must be no more than 1000")
            Integer limit,

            @RequestParam(required = false) @Min(value = 0, message = "offset must be at least 0")
            Integer offset,

            @RequestParam(required = false) String sort
    ) {
        log.info("Received temperature query from {} to {}", startTime, endTime);
        return temperatureDataService.query(new TemperatureQueryRequest(startTime, endTime, limit, offset, sort));
    }
}
