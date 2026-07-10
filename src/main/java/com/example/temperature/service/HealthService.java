package com.example.temperature.service;

import com.example.temperature.dto.response.HealthResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public HealthResponse health() {
        return new HealthResponse("UP");
    }
}
