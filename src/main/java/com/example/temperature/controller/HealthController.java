package com.example.temperature.controller;

import com.example.temperature.dto.response.HealthResponse;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping(path = "/v1/health")
    @ResponseStatus(HttpStatus.OK)
    public HealthResponse health() {
        HealthComponent health = healthEndpoint.health();
        String status = Status.UP.equals(health.getStatus()) ? "UP" : health.getStatus().getCode();
        return new HealthResponse(status);
    }
}
