package com.example.temperature.api;

import com.example.temperature.exception.ApiException;
import com.example.temperature.exception.ErrorCode;
import com.example.temperature.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public Map<String, String> health() {
        if (!healthService.isHealthy()) {
            throw new ApiException(ErrorCode.SERVICE_UNAVAILABLE, "Service is not healthy");
        }
        return Map.of("status", "UP");
    }
}
