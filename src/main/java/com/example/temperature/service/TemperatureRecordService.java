package com.example.temperature.service;

import com.example.temperature.dto.error.ErrorDetail;
import com.example.temperature.dto.request.IngestTemperatureRecordsRequest;
import com.example.temperature.dto.response.IngestTemperatureRecordsResponse;
import com.example.temperature.dto.response.TemperatureRecordResponse;
import com.example.temperature.dto.response.TemperatureRecordsResponse;
import com.example.temperature.entity.TemperatureRecordEntity;
import com.example.temperature.exception.InvalidQueryParameterException;
import com.example.temperature.mapper.TemperatureRecordMapper;
import com.example.temperature.repository.TemperatureRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemperatureRecordService {

    private static final Logger log = LoggerFactory.getLogger(TemperatureRecordService.class);
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 10_000;

    private final TemperatureRecordRepository repository;
    private final TemperatureRecordMapper mapper;

    public TemperatureRecordService(TemperatureRecordRepository repository, TemperatureRecordMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public IngestTemperatureRecordsResponse ingest(IngestTemperatureRecordsRequest request) {
        validateFiniteTemperatures(request);
        List<TemperatureRecordEntity> entities = request.records().stream()
                .map(mapper::toEntity)
                .toList();
        repository.saveAll(entities);
        log.info("Persisted {} temperature records", entities.size());
        return new IngestTemperatureRecordsResponse(entities.size());
    }

    @Transactional(readOnly = true)
    public TemperatureRecordsResponse query(OffsetDateTime startTime, OffsetDateTime endTime, Integer page, Integer limit) {
        int resolvedPage = page == null ? DEFAULT_PAGE : page;
        int resolvedLimit = limit == null ? DEFAULT_LIMIT : limit;
        validateQuery(startTime, endTime, resolvedPage, resolvedLimit);

        Pageable pageable = PageRequest.of(
                resolvedPage - 1,
                resolvedLimit,
                Sort.by(Sort.Order.asc("recordedAt"), Sort.Order.asc("id"))
        );
        Page<TemperatureRecordEntity> results = repository.findByRecordedAtBetween(
                startTime.toInstant(),
                endTime.toInstant(),
                pageable
        );
        List<TemperatureRecordResponse> records = results.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        return new TemperatureRecordsResponse(records, resolvedPage, resolvedLimit, records.size());
    }

    private void validateFiniteTemperatures(IngestTemperatureRecordsRequest request) {
        List<ErrorDetail> details = new ArrayList<>();
        for (int i = 0; i < request.records().size(); i++) {
            BigDecimal temperature = request.records().get(i).temperature();
            if (temperature == null) {
                continue;
            }
            if (temperature.toString().equalsIgnoreCase("NaN")) {
                details.add(new ErrorDetail("records[" + i + "].temperature", "temperature must be a finite decimal value"));
            }
        }
        if (!details.isEmpty()) {
            throw new InvalidQueryParameterException("One or more temperature records failed validation", details);
        }
    }

    private void validateQuery(OffsetDateTime startTime, OffsetDateTime endTime, int page, int limit) {
        List<ErrorDetail> details = new ArrayList<>();
        if (endTime.isBefore(startTime)) {
            details.add(new ErrorDetail("endTime", "endTime must be greater than or equal to startTime"));
        }
        if (page < 1) {
            details.add(new ErrorDetail("page", "page must be greater than or equal to 1"));
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            details.add(new ErrorDetail("limit", "limit must be between 1 and 10000"));
        }
        if (!details.isEmpty()) {
            throw new InvalidQueryParameterException("Invalid query parameter", details);
        }
    }
}
