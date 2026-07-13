package com.example.temperature.service;

import com.example.temperature.dto.error.ErrorDetail;
import com.example.temperature.dto.request.IngestTemperatureDataPointsRequest;
import com.example.temperature.dto.response.IngestTemperatureDataPointsResponse;
import com.example.temperature.dto.response.PaginationResponse;
import com.example.temperature.dto.response.QueryTemperatureDataPointsResponse;
import com.example.temperature.dto.response.TemperatureDataPointResponse;
import com.example.temperature.entity.TemperatureDataPointEntity;
import com.example.temperature.exception.InvalidQueryParameterException;
import com.example.temperature.mapper.TemperatureDataPointMapper;
import com.example.temperature.repository.TemperatureDataPointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemperatureDataPointService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureDataPointService.class);

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 1000;
    private static final int DEFAULT_OFFSET = 0;

    private final TemperatureDataPointRepository repository;
    private final TemperatureDataPointMapper mapper;

    public TemperatureDataPointService(
            TemperatureDataPointRepository repository,
            TemperatureDataPointMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public IngestTemperatureDataPointsResponse ingest(IngestTemperatureDataPointsRequest request) {
        List<TemperatureDataPointEntity> entities = request.data()
                .stream()
                .map(mapper::toEntity)
                .toList();

        List<TemperatureDataPointEntity> savedEntities = repository.saveAll(entities);
        List<TemperatureDataPointResponse> responseData = savedEntities
                .stream()
                .map(mapper::toResponse)
                .toList();

        LOGGER.info("Accepted {} temperature data point(s)", responseData.size());

        return new IngestTemperatureDataPointsResponse(responseData.size(), responseData);
    }

    @Transactional(readOnly = true)
    public QueryTemperatureDataPointsResponse query(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp,
            Integer requestedLimit,
            Integer requestedOffset
    ) {
        int limit = requestedLimit == null ? DEFAULT_LIMIT : requestedLimit;
        int offset = requestedOffset == null ? DEFAULT_OFFSET : requestedOffset;

        validateQueryParameters(startTimestamp, endTimestamp, limit, offset);

        List<TemperatureDataPointResponse> data = repository
                .findByTimestampRange(startTimestamp, endTimestamp, limit, offset)
                .stream()
                .map(mapper::toResponse)
                .toList();

        LOGGER.info(
                "Returned {} temperature data point(s) for startTimestamp={}, endTimestamp={}, limit={}, offset={}",
                data.size(),
                startTimestamp,
                endTimestamp,
                limit,
                offset
        );

        return new QueryTemperatureDataPointsResponse(
                data,
                new PaginationResponse(limit, offset, data.size())
        );
    }

    private void validateQueryParameters(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp,
            int limit,
            int offset
    ) {
        List<ErrorDetail> details = new ArrayList<>();

        if (limit < 1 || limit > MAX_LIMIT) {
            details.add(new ErrorDetail("limit", "limit must be an integer from 1 to 1000"));
        }

        if (offset < 0) {
            details.add(new ErrorDetail("offset", "offset must be an integer greater than or equal to 0"));
        }

        if (startTimestamp != null && endTimestamp != null && startTimestamp.isAfter(endTimestamp)) {
            details.add(new ErrorDetail(
                    "startTimestamp",
                    "startTimestamp must be less than or equal to endTimestamp"
            ));
        }

        if (!details.isEmpty()) {
            throw new InvalidQueryParameterException(details);
        }
    }
}
