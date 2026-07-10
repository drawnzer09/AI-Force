package com.example.temperature.service;

import com.example.temperature.config.ApplicationProperties;
import com.example.temperature.dto.request.TemperatureBatchRequest;
import com.example.temperature.dto.response.TemperatureBatchResponse;
import com.example.temperature.dto.response.TemperatureRecordQueryResponse;
import com.example.temperature.dto.response.TemperatureRecordResponse;
import com.example.temperature.entity.TemperatureRecordEntity;
import com.example.temperature.exception.InvalidQueryException;
import com.example.temperature.exception.PayloadTooLargeException;
import com.example.temperature.mapper.TemperatureRecordMapper;
import com.example.temperature.repository.TemperatureRecordRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemperatureRecordService {

    private static final Sort QUERY_SORT = Sort.by(
            Sort.Order.asc("recordedAt"),
            Sort.Order.asc("id")
    );

    private final TemperatureRecordRepository repository;
    private final TemperatureRecordMapper mapper;
    private final ApplicationProperties properties;

    public TemperatureRecordService(
            TemperatureRecordRepository repository,
            TemperatureRecordMapper mapper,
            ApplicationProperties properties
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Transactional
    public TemperatureBatchResponse ingest(TemperatureBatchRequest request) {
        int recordCount = request.records().size();
        if (recordCount > properties.maxBatchSize()) {
            throw new PayloadTooLargeException(
                    "records",
                    "records must contain no more than " + properties.maxBatchSize() + " items"
            );
        }

        List<TemperatureRecordEntity> entities = request.records().stream()
                .map(mapper::toEntity)
                .toList();

        List<TemperatureRecordEntity> saved = repository.saveAll(entities);
        return new TemperatureBatchResponse(saved.size());
    }

    @Transactional(readOnly = true)
    public TemperatureRecordQueryResponse query(
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            int page,
            int pageSize
    ) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new InvalidQueryException("startTime", "startTime must be before or equal to endTime");
        }

        int appliedPageSize = Math.min(pageSize, properties.maxPageSize());
        Pageable pageable = PageRequest.of(page, appliedPageSize, QUERY_SORT);
        Page<TemperatureRecordEntity> resultPage = repository.findByOptionalRecordedAtRange(startTime, endTime, pageable);

        List<TemperatureRecordResponse> records = resultPage.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return new TemperatureRecordQueryResponse(
                records,
                page,
                appliedPageSize,
                records.size(),
                resultPage.hasNext()
        );
    }

    public int defaultPageSize() {
        return properties.defaultPageSize();
    }
}
