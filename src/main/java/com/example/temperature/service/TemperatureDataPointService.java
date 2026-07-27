package com.example.temperature.service;

import com.example.temperature.api.dto.IngestTemperatureDataPointsRequest;
import com.example.temperature.api.dto.IngestTemperatureDataPointsResponse;
import com.example.temperature.api.dto.PageMetadataResponse;
import com.example.temperature.api.dto.QueryTemperatureDataPointsResponse;
import com.example.temperature.api.dto.TemperatureDataPointResponse;
import com.example.temperature.config.ApplicationProperties;
import com.example.temperature.exception.DataAccessUnavailableException;
import com.example.temperature.exception.InvalidRequestException;
import com.example.temperature.mapper.TemperatureDataPointMapper;
import com.example.temperature.persistence.entity.TemperatureDataPointEntity;
import com.example.temperature.persistence.repository.TemperatureDataPointRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemperatureDataPointService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureDataPointService.class);

    private final TemperatureDataPointRepository repository;
    private final TemperatureDataPointMapper mapper;
    private final ApplicationProperties applicationProperties;

    public TemperatureDataPointService(
            TemperatureDataPointRepository repository,
            TemperatureDataPointMapper mapper,
            ApplicationProperties applicationProperties
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.applicationProperties = applicationProperties;
    }

    @Transactional
    public IngestTemperatureDataPointsResponse ingest(IngestTemperatureDataPointsRequest request) {
        List<TemperatureDataPointEntity> entities = request.dataPoints().stream()
                .map(mapper::toEntity)
                .toList();

        try {
            repository.saveAll(entities);
            LOGGER.info("Ingested {} temperature data points", entities.size());
            return new IngestTemperatureDataPointsResponse(entities.size());
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to persist temperature data points", ex);
            throw new DataAccessUnavailableException("Temperature data cannot be persisted at this time", ex);
        }
    }

    @Transactional(readOnly = true)
    public QueryTemperatureDataPointsResponse query(
            OffsetDateTime fromTimestamp,
            OffsetDateTime toTimestamp,
            Integer page,
            Integer size
    ) {
        int resolvedPage = page == null ? 0 : page;
        int resolvedSize = size == null ? applicationProperties.pagination().defaultPageSize() : size;
        validateQuery(fromTimestamp, toTimestamp, resolvedPage, resolvedSize);

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize);

        try {
            Page<TemperatureDataPointEntity> resultPage = repository.findByOptionalTimestampRange(
                    fromTimestamp,
                    toTimestamp,
                    pageable
            );
            List<TemperatureDataPointResponse> dataPoints = resultPage.getContent().stream()
                    .map(mapper::toResponse)
                    .toList();
            return new QueryTemperatureDataPointsResponse(
                    dataPoints,
                    new PageMetadataResponse(resolvedPage, resolvedSize, dataPoints.size())
            );
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to query temperature data points", ex);
            throw new DataAccessUnavailableException("Temperature data cannot be read at this time", ex);
        }
    }

    private void validateQuery(OffsetDateTime fromTimestamp, OffsetDateTime toTimestamp, int page, int size) {
        if (page < 0) {
            throw new InvalidRequestException("INVALID_PAGINATION", "Invalid pagination value", "page", "page must be greater than or equal to 0");
        }
        if (size < 1) {
            throw new InvalidRequestException("INVALID_PAGINATION", "Invalid pagination value", "size", "size must be greater than or equal to 1");
        }
        if (size > applicationProperties.pagination().maxPageSize()) {
            throw new InvalidRequestException(
                    "INVALID_PAGINATION",
                    "Invalid pagination value",
                    "size",
                    "size must be less than or equal to " + applicationProperties.pagination().maxPageSize()
            );
        }
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp.isAfter(toTimestamp)) {
            throw new InvalidRequestException(
                    "INVALID_TIMESTAMP_RANGE",
                    "Invalid timestamp range",
                    "fromTimestamp",
                    "fromTimestamp must be less than or equal to toTimestamp"
            );
        }
    }
}
