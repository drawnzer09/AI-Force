package com.example.temperature.service;

import com.example.temperature.dto.response.HealthResponse;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class HealthService {

    private final DataSource dataSource;

    public HealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public HealthResponse health() {
        if (isDatabaseConnected()) {
            return new HealthResponse("healthy", "connected");
        }
        return new HealthResponse("unhealthy", "unavailable");
    }

    private boolean isDatabaseConnected() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (SQLException ex) {
            return false;
        }
    }
}
