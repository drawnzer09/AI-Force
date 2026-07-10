package com.example.temperature.service;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final HealthEndpoint healthEndpoint;

    public HealthService(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    public boolean isUp() {
        return Status.UP.equals(healthEndpoint.health().getStatus());
    }
}
