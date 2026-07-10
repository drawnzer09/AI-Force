package com.example.temperature.service;

import com.example.temperature.exception.PersistenceUnavailableException;
import com.example.temperature.repository.TemperatureRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthService.class);

    private final TemperatureRecordRepository repository;

    public HealthService(TemperatureRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public void verifyHealthy() {
        try {
            repository.count();
        } catch (DataAccessException ex) {
            LOGGER.error("Health check failed because persistence is unavailable", ex);
            throw new PersistenceUnavailableException("Persistence layer is unavailable", ex);
        }
    }
}
