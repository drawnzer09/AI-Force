package com.example.temperature.service;

import com.example.temperature.exception.PersistenceUnavailableException;
import com.example.temperature.repository.TemperatureReadingRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthService {

    private final TemperatureReadingRepository repository;

    public HealthService(TemperatureReadingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public void verifyHealthy() {
        try {
            if (!repository.isAvailable()) {
                throw new PersistenceUnavailableException("Persistence is unavailable");
            }
        } catch (DataAccessException ex) {
            throw new PersistenceUnavailableException("Persistence is unavailable", ex);
        }
    }
}
