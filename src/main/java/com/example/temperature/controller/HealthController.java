package com.example.temperature.controller;

import com.example.temperature.dto.response.HealthResponse;
import com.example.temperature.service.HealthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/health", produces = MediaType.APPLICATION_JSON_VALUE)
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public HealthResponse health() {
        return healthService.health();
    }
}
