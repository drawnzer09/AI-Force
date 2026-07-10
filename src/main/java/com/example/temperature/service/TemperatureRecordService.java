package com.example.temperature.service;

import com.example.temperature.dto.query.TemperatureRecordQuery;
import com.example.temperature.dto.request.TemperatureBatchRequest;
import com.example.temperature.dto.response.PaginationResponse;
import com.example.temperature.dto.response.TemperatureBatchResponse;
import com.example.temperature.dto.response.TemperatureQueryResponse;
import com.example.temperature.dto.response.TemperatureRecordResponse;
import com.example.temperature.entity.TemperatureRecordEntity;
import com.example.temperature.exception.QueryValidationException;
import com.example.temperature.mapper.TemperatureRecordMapper;
import com.example.temperature.repository.TemperatureRecordRepository;
import com.example.temperature.repository.TemperatureRecordSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemperatureRecordService {

    private final TemperatureRecordRepository temperatureRecordRepository;
    private final TemperatureRecordMapper temperatureRecordMapper;

    public TemperatureRecordService(
            TemperatureRecordRepository temperatureRecordRepository,
            TemperatureRecordMapper temperatureRecordMapper
    ) {
        this.temperatureRecordRepository = temperatureRecordRepository;
        this.temperatureRecordMapper = temperatureRecordMapper;
    }

    @Transactional
    public TemperatureBatchResponse createBatch(TemperatureBatchRequest request) {
        List<TemperatureRecordEntity> entities = request.getRecords()
                .stream()
                .map(temperatureRecordMapper::toEntity)
                .toList();

        List<TemperatureRecordResponse> records = temperatureRecordRepository.saveAllAndFlush(entities)
                .stream()
                .map(temperatureRecordMapper::toResponse)
                .toList();

        return new TemperatureBatchResponse(records.size(), records);
    }

    @Transactional(readOnly = true)
    public TemperatureQueryResponse query(TemperatureRecordQuery query) {
        validateQuery(query);

        Sort sort = Sort.by(query.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC, "timestamp");
        List<TemperatureRecordResponse> records = temperatureRecordRepository
                .findAll(
                        TemperatureRecordSpecifications.timestampBetween(
                                query.getStartTimestamp(),
                                query.getEndTimestamp()
                        ),
                        sort
                )
                .stream()
                .skip(query.getOffset())
                .limit(query.getLimit())
                .map(temperatureRecordMapper::toResponse)
                .toList();

        return new TemperatureQueryResponse(
                records,
                new PaginationResponse(query.getLimit(), query.getOffset(), records.size())
        );
    }

    private void validateQuery(TemperatureRecordQuery query) {
        if (query.getLimit() < 1 || query.getLimit() > TemperatureRecordQuery.MAX_LIMIT) {
            throw new QueryValidationException("limit", "limit must be between 1 and 1000");
        }
        if (query.getOffset() < 0) {
            throw new QueryValidationException("offset", "offset must be greater than or equal to 0");
        }
        if (!"timestamp".equals(query.getSort()) && !"-timestamp".equals(query.getSort())) {
            throw new QueryValidationException("sort", "sort must be either timestamp or -timestamp");
        }
        if (query.getStartTimestamp() != null
                && query.getEndTimestamp() != null
                && query.getStartTimestamp().isAfter(query.getEndTimestamp())) {
            throw new QueryValidationException(
                    "startTimestamp",
                    "startTimestamp must be before or equal to endTimestamp"
            );
        }
    }
}
