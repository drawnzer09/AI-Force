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

        int normalizedPage = normalizePage(page);
        int normalizedPageSize = normalizePageSize(pageSize);
        Sort.Direction direction = normalizeSort(sort);

        PageRequest pageRequest = PageRequest.of(
                normalizedPage - 1,
                normalizedPageSize,
                Sort.by(direction, "measurementTimestamp").and(Sort.by(direction, "id"))
        );

        try {
            Page<TemperatureRecordEntity> resultPage =
                    repository.findByOptionalMeasurementTimestampRange(from, to, pageRequest);

            List<TemperatureRecordResponse> records = resultPage.getContent()
                    .stream()
                    .map(mapper::toResponse)
                    .toList();

            PaginationResponse pagination = new PaginationResponse(
                    normalizedPage,
                    normalizedPageSize,
                    records.size(),
                    resultPage.hasNext()
            );

            LOGGER.info(
                    "Queried temperature records from={} to={} page={} pageSize={} sort={} returnedCount={}",
                    from,
                    to,
                    normalizedPage,
                    normalizedPageSize,
                    sort == null ? SORT_ASC : sort,
                    records.size()
            );

            return new TemperatureQueryResponse(records, pagination);
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to query temperature records", ex);
            throw new PersistenceUnavailableException("Persistence layer is unavailable", ex);
        }
    }

    private void validateRange(OffsetDateTime from, OffsetDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidQueryParameterException("from", "from must be earlier than or equal to to");
        }
    }

    private int normalizePage(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        if (page < 1) {
            throw new InvalidQueryParameterException("page", "page must be greater than or equal to 1");
        }
        return page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize < 1) {
            throw new InvalidQueryParameterException("pageSize", "pageSize must be greater than or equal to 1");
        }
        if (pageSize > MAX_PAGE_SIZE) {
            throw new InvalidQueryParameterException("pageSize", "pageSize must be less than or equal to 1000");
        }
        return pageSize;
    }

    private Sort.Direction normalizeSort(String sort) {
        if (sort == null || sort.isBlank() || SORT_ASC.equals(sort)) {
            return Sort.Direction.ASC;
        }
        if (SORT_DESC.equals(sort)) {
            return Sort.Direction.DESC;
        }
        throw new InvalidQueryParameterException("sort", "sort must be one of: timestamp, -timestamp");
    }
}
