package com.example.temperature.service;

import com.example.temperature.dto.request.TemperatureBatchRequest;
import com.example.temperature.dto.response.PaginationResponse;
import com.example.temperature.dto.response.TemperatureBatchResponse;
import com.example.temperature.dto.response.TemperatureQueryResponse;
import com.example.temperature.dto.response.TemperatureRecordResponse;
import com.example.temperature.entity.TemperatureRecordEntity;
import com.example.temperature.exception.InvalidQueryParameterException;
import com.example.temperature.exception.PersistenceUnavailableException;
import com.example.temperature.mapper.TemperatureRecordMapper;
import com.example.temperature.repository.TemperatureRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TemperatureRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureRecordService.class);
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;
    private static final String SORT_ASC = "timestamp";
    private static final String SORT_DESC = "-timestamp";

    private final TemperatureRecordRepository repository;
    private final TemperatureRecordMapper mapper;

    public TemperatureRecordService(TemperatureRecordRepository repository, TemperatureRecordMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public TemperatureBatchResponse ingest(TemperatureBatchRequest request) {
        try {
            List<TemperatureRecordEntity> entities = request.getRecords()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

            repository.saveAll(entities);
            repository.flush();

            LOGGER.info("Persisted {} temperature records", entities.size());
            return new TemperatureBatchResponse(entities.size());
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to persist temperature records", ex);
            throw new PersistenceUnavailableException("Persistence layer is unavailable", ex);
        }
    }

    @Transactional(readOnly = true)
    public TemperatureQueryResponse query(
            OffsetDateTime from,
            OffsetDateTime to,
            Integer page,
            Integer pageSize,
            String sort
    ) {
        validateRange(from, to);

        int normalized