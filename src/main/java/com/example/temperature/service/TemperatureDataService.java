package com.example.temperature.service;

import com.example.temperature.dto.PageMetadataResponse;
import com.example.temperature.dto.TemperatureBatchRequest;
import com.example.temperature.dto.TemperatureIngestResponse;
import com.example.temperature.dto.TemperatureQueryRequest;
import com.example.temperature.dto.TemperatureQueryResponse;
import com.example.temperature.entity.TemperatureReadingEntity;
import com.example.temperature.mapper.TemperatureReadingMapper;
import com.example.temperature.repository.TemperatureReadingRepository;
import com.example.temperature.validation.QueryValidation;
import com.example.temperature.validation.ValidatedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemperatureDataService {

    private static final Logger log = LoggerFactory.getLogger(TemperatureDataService.class);

    private final TemperatureReadingRepository repository;
    private final TemperatureReadingMapper mapper;
    private final QueryValidation queryValidation;

    public TemperatureDataService(
            TemperatureReadingRepository repository,
            TemperatureReadingMapper mapper,
            QueryValidation queryValidation
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryValidation = queryValidation;
    }

    @Transactional
    public TemperatureIngestResponse ingest(TemperatureBatchRequest request) {
        List<TemperatureReadingEntity> readings = mapper.toEntities(request.dataPoints());
        repository.insertAll(readings);
        log.info("Stored {} temperature readings", readings.size());
        return new TemperatureIngestResponse(readings.size());
    }

    @Transactional(readOnly = true)
    public TemperatureQueryResponse query(TemperatureQueryRequest request) {
        ValidatedQuery validatedQuery = queryValidation.validate(request);
        List<TemperatureReadingEntity> readings = repository.findByTimestampRange(
                validatedQuery.startTime(),
                validatedQuery.endTime(),
                validatedQuery.limit(),
                validatedQuery.offset(),
                validatedQuery.sortOrder()
        );
        long total = repository.countByTimestampRange(validatedQuery.startTime(), validatedQuery.endTime());

        return new TemperatureQueryResponse(
                mapper.toDtos(readings),
                new PageMetadataResponse(validatedQuery.limit(), validatedQuery.offset(), total)
        );
    }
}
