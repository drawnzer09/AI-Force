package com.example.temperature.controller;

import com.example.temperature.dto.response.HealthResponse;
import com.example.temperature.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/health", produces = "application/json")
public class HealthController {

    private final HealthService service;

    public HealthController(HealthService service) {
        this.service = service;
    }

    @GetMapping
    public HealthResponse health() {
        service.verifyHealthy();
        return new HealthResponse("UP");
    }
}
