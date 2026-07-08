package com.example.temperature.service;

import com.example.temperature.dto.temperature.IngestTemperatureReadingsRequestDto;
import com.example.temperature.dto.temperature.IngestTemperatureReadingsResponseDto;
import com.example.temperature.dto.temperature.TemperatureReadingsResponseDto;
import com.example.temperature.exception.InvalidTimestampRangeException;
import com.example.temperature.mapper.TemperatureReadingMapper;
import com.example.temperature.persistence.TemperatureReadingRow;
import com.example.temperature.repository.TemperatureReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TemperatureReadingService {

    public static final int DEFAULT_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;

    private final TemperatureReadingRepository repository;
    private final TemperatureReadingMapper mapper;

    public TemperatureReadingService(TemperatureReadingRepository repository, TemperatureReadingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public IngestTemperatureReadingsResponseDto ingest(IngestTemperatureReadingsRequestDto request) {
        List<TemperatureReadingRow> rows = mapper.toRows(request.records());
        int acceptedCount = repository.saveAll(rows);
        return new IngestTemperatureReadingsResponseDto(acceptedCount);
    }

    @Transactional(readOnly = true)
    public TemperatureReadingsResponseDto query(OffsetDateTime fromTimestamp,
                                                OffsetDateTime toTimestamp,
                                                Integer limit,
                                                Integer offset) {
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp.isAfter(toTimestamp)) {
            throw new InvalidTimestampRangeException("fromTimestamp must be less than or equal to toTimestamp");
        }

        int appliedLimit = limit == null ? DEFAULT_LIMIT : limit;
        int appliedOffset = offset == null ? DEFAULT_OFFSET : offset;
        List<TemperatureReadingRow> rows = repository.findByTimestampRange(
                fromTimestamp,
                toTimestamp,
                appliedLimit,
                appliedOffset
        );

        return new TemperatureReadingsResponseDto(
                mapper.toResponseDtos(rows),
                rows.size(),
                appliedLimit,
                appliedOffset
        );
    }
}
