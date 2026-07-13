package com.example.temperature.service;

import com.example.temperature.dto.request.IngestTemperatureRecordsRequest;
import com.example.temperature.dto.response.IngestTemperatureRecordsResponse;
import com.example.temperature.dto.response.PageMetadataResponse;
import com.example.temperature.dto.response.TemperatureRecordPageResponse;
import com.example.temperature.dto.response.TemperatureRecordResponse;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TemperatureRecordService {

    private static final Logger log = LoggerFactory.getLogger(TemperatureRecordService.class);

    private final TemperatureRecordRepository repository;
    private final TemperatureRecordMapper mapper;

    public TemperatureRecordService(TemperatureRecordRepository repository, TemperatureRecordMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public IngestTemperatureRecordsResponse ingest(IngestTemperatureRecordsRequest request) {
        List<TemperatureRecordEntity> entities = request.records()
                .stream()
                .map(mapper::toEntity)
                .toList();

        List<TemperatureRecordEntity> storedRecords = repository.saveAll(entities);
        log.info("Stored {} temperature records", storedRecords.size());

        return new IngestTemperatureRecordsResponse(storedRecords.size());
    }

    @Transactional(readOnly = true)
    public TemperatureRecordPageResponse query(
            OffsetDateTime from,
            OffsetDateTime to,
            int page,
            int size,
            String sort
    ) {
        validateTimestampRange(from, to);

        Sort.Direction direction = parseSortDirection(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "recordedAt").and(Sort.by(direction, "id")));

        Page<TemperatureRecordEntity> resultPage;
        if (from != null && to != null) {
            resultPage = repository.findByRecordedAtBetween(from, to, pageable);
        } else if (from != null) {
            resultPage = repository.findByRecordedAtGreaterThanEqual(from, pageable);
        } else if (to != null) {
            resultPage = repository.findByRecordedAtLessThanEqual(to, pageable);
        } else {
            resultPage = repository.findAll(pageable);
        }

        List<TemperatureRecordResponse> records = resultPage.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        log.info(
                "Queried temperature records with from={}, to={}, page={}, size={}, sort={}, returnedCount={}",
                from,
                to,
                page,
                size,
                sort,
                records.size()
        );

        return new TemperatureRecordPageResponse(
                records,
                new PageMetadataResponse(page, size, records.size())
        );
    }

    private void validateTimestampRange(OffsetDateTime from, OffsetDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidQueryParameterException(
                    "INVALID_TIMESTAMP_RANGE",
                    "Invalid timestamp range",
                    "from",
                    "from must be earlier than or equal to to"
            );
        }
    }

    private Sort.Direction parseSortDirection(String sort) {
        String normalizedSort = sort.toLowerCase(Locale.ROOT);
        if ("asc".equals(normalizedSort)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equals(normalizedSort)) {
            return Sort.Direction.DESC;
        }
        throw new InvalidQueryParameterException(
                "INVALID_SORT_DIRECTION",
                "Invalid sort direction",
                "sort",
                "sort must be either asc or desc"
        );
    }
}
