package com.example.temperature.service;

import com.example.temperature.api.dto.PaginationResponse;
import com.example.temperature.api.dto.TemperatureIngestRequest;
import com.example.temperature.api.dto.TemperatureIngestResponse;
import com.example.temperature.api.dto.TemperatureQueryResponse;
import com.example.temperature.api.dto.TemperatureRecordResponse;
import com.example.temperature.mapper.TemperatureMeasurementMapper;
import com.example.temperature.persistence.entity.TemperatureMeasurementEntity;
import com.example.temperature.repository.TemperatureMeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
public class TemperatureService {

    private static final Logger log = LoggerFactory.getLogger(TemperatureService.class);

    private final TemperatureMeasurementRepository repository;
    private final TemperatureMeasurementMapper mapper;

    public TemperatureService(TemperatureMeasurementRepository repository, TemperatureMeasurementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public TemperatureIngestResponse ingest(TemperatureIngestRequest request) {
        List<TemperatureMeasurementEntity> entities = request.records().stream()
                .map(mapper::toEntity)
                .toList();

        repository.saveAll(entities);
        repository.flush();

        Instant earliest = entities.stream()
                .map(TemperatureMeasurementEntity::getMeasurementTimestamp)
                .min(Comparator.naturalOrder())
                .orElseThrow();

        Instant latest = entities.stream()
                .map(TemperatureMeasurementEntity::getMeasurementTimestamp)
                .max(Comparator.naturalOrder())
                .orElseThrow();

        log.info("Stored {} temperature records", entities.size());

        return new TemperatureIngestResponse(
                entities.size(),
                OffsetDateTime.ofInstant(earliest, ZoneOffset.UTC),
                OffsetDateTime.ofInstant(latest, ZoneOffset.UTC)
        );
    }

    @Transactional(readOnly = true)
    public TemperatureQueryResponse query(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp,
            int limit,
            long offset
    ) {
        List<TemperatureRecordResponse> records = repository.findByTimestampRange(
                        startTimestamp.toInstant(),
                        endTimestamp.toInstant(),
                        limit,
                        offset
                )
                .stream()
                .map(mapper::toResponse)
                .toList();

        log.info("Returned {} temperature records", records.size());

        return new TemperatureQueryResponse(
                records,
                new PaginationResponse(limit, offset, records.size())
        );
    }
}
