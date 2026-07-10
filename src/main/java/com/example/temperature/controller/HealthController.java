package com.example.temperature.controller;

import com.example.temperature.dto.response.HealthResponse;
import com.example.temperature.service.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping(value = "/v1/health", produces = "application/json")
    public HealthResponse health() {
        if (!healthService.isUp()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service is unavailable");
        }
        return new HealthResponse("UP");
    }
}
