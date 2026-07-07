package com.example.temperature.service;

import com.example.temperature.dto.request.TemperatureBatchRequest;
import com.example.temperature.dto.response.PaginationResponse;
import com.example.temperature.dto.response.TemperatureBatchResponse;
import com.example.temperature.dto.response.TemperatureQueryResponse;
import com.example.temperature.dto.response.TemperatureRecordResponse;
import com.example.temperature.entity.TemperatureRecordEntity;
import com.example.temperature.mapper.TemperatureRecordMapper;
import com.example.temperature.repository.TemperatureRecordRepository;
import com.example.temperature.validation.TemperatureQueryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TemperatureRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureRecordService.class);

    private final TemperatureRecordRepository repository;
    private final TemperatureRecordMapper mapper;
    private final TemperatureQueryValidator queryValidator;

    public TemperatureRecordService(
            TemperatureRecordRepository repository,
            TemperatureRecordMapper mapper,
            TemperatureQueryValidator queryValidator
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.queryValidator = queryValidator;
    }

    @Transactional
    public TemperatureBatchResponse ingest(TemperatureBatchRequest request) {
        try {
            List<TemperatureRecordEntity> entities = request.getRecords().stream()
                    .map(mapper::toEntity)
                    .toList();
            List<TemperatureRecordEntity> saved = repository.saveAll(entities);
            List<TemperatureRecordResponse> records = saved.stream()
                    .map(mapper::toResponse)
                    .toList();
            LOGGER.info("Accepted {} temperature records", records.size());
            return new TemperatureBatchResponse(records.size(), records);
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to persist temperature records", ex);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public TemperatureQueryResponse query(OffsetDateTime startTime, OffsetDateTime endTime, Integer limit, Integer offset, String sort) {
        TemperatureQueryValidator.ValidatedQuery query = queryValidator.validate(startTime, endTime, limit, offset, sort);
        Sort.Direction direction = "-timestamp".equals(query.sort()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort springSort = Sort.by(direction, "readingTimestamp").and(Sort.by(direction, "id"));
        Pageable pageable = new OffsetLimitPageRequest(query.offset(), query.limit(), springSort);

        List<TemperatureRecordResponse> records = repository
                .findByOptionalTimeRange(query.startTime(), query.endTime(), pageable)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return new TemperatureQueryResponse(
                records,
                new PaginationResponse(query.limit(), query.offset(), records.size())
        );
    }

    private static final class OffsetLimitPageRequest implements Pageable {
        private final int offset;
        private final int limit;
        private final Sort sort;

        private OffsetLimitPageRequest(int offset, int limit, Sort sort) {
            this.offset = offset;
            this.limit = limit;
            this.sort = sort;
        }

        @Override
        public int getPageNumber() {
            return offset / limit;
        }

        @Override
        public int getPageSize() {
            return limit;
        }

        @Override
        public long getOffset() {
            return offset;
        }

        @Override
        public Sort getSort() {
            return sort;
        }

        @Override
        public Pageable next() {
            return new OffsetLimitPageRequest(offset + limit, limit, sort);
        }

        @Override
        public Pageable previousOrFirst() {
            return hasPrevious() ? new OffsetLimitPageRequest(Math.max(offset - limit, 0), limit, sort) : first();
        }

        @Override
        public Pageable first() {
            return new OffsetLimitPageRequest(0, limit, sort);
        }

        @Override
        public Pageable withPage(int pageNumber) {
            if (pageNumber < 0) {
                throw new IllegalArgumentException("Page index must not be less than zero");
            }
            return new OffsetLimitPageRequest(pageNumber * limit, limit, sort);
        }

        @Override
        public boolean hasPrevious() {
            return offset > 0;
        }
    }
}
