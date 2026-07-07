package com.example.temperature.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final ObjectProvider<HealthEndpoint> healthEndpointProvider;

    public HealthService(ObjectProvider<HealthEndpoint> healthEndpointProvider) {
        this.healthEndpointProvider = healthEndpointProvider;
    }

    public boolean isReady() {
        HealthEndpoint healthEndpoint = healthEndpointProvider.getIfAvailable();
        if (healthEndpoint == null) {
            return true;
        }
        HealthComponent health = healthEndpoint.health();
        return Status.UP.equals(health.getStatus());
    }
}
