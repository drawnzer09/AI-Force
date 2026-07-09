package com.example.temperature.service;

import com.example.temperature.dto.request.IngestTemperatureReadingsRequest;
import com.example.temperature.dto.request.TemperatureReadingQueryRequest;
import com.example.temperature.dto.response.IngestTemperatureReadingsResponse;
import com.example.temperature.dto.response.PaginationResponse;
import com.example.temperature.dto.response.TemperatureReadingQueryResponse;
import com.example.temperature.dto.response.TemperatureReadingResponse;
import com.example.temperature.entity.TemperatureReadingEntity;
import com.example.temperature.mapper.TemperatureReadingMapper;
import com.example.temperature.repository.TemperatureReadingRepository;
import com.example.temperature.validation.TemperatureReadingValidator;
import com.example.temperature.validation.TimeRangeValidator;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemperatureReadingServiceImpl implements TemperatureReadingService {

    private final TemperatureReadingRepository repository;
    private final TemperatureReadingMapper mapper;
    private final TemperatureReadingValidator readingValidator;
    private final TimeRangeValidator timeRangeValidator;

    public TemperatureReadingServiceImpl(
            TemperatureReadingRepository repository,
            TemperatureReadingMapper mapper,
            TemperatureReadingValidator readingValidator,
            TimeRangeValidator timeRangeValidator
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.readingValidator = readingValidator;
        this.timeRangeValidator = timeRangeValidator;
    }

    @Override
    @Transactional
    public IngestTemperatureReadingsResponse ingest(IngestTemperatureReadingsRequest request) {
        readingValidator.validate(request.getReadings());
        List<TemperatureReadingEntity> entities = request.getReadings().stream()
                .map(mapper::toEntity)
                .toList();
        List<TemperatureReadingEntity> saved = repository.saveAll(entities);
        List<TemperatureReadingResponse> responses = saved.stream()
                .map(mapper::toResponse)
                .toList();
        return new IngestTemperatureReadingsResponse(request.getReadings().size(), saved.size(), responses);
    }

    @Override
    @Transactional(readOnly = true)
    public TemperatureReadingQueryResponse query(TemperatureReadingQueryRequest request) {
        timeRangeValidator.validate(request);
        int limit = timeRangeValidator.normalizedLimit(request);
        int offset = timeRangeValidator.normalizedOffset(request);
        String sortValue = timeRangeValidator.normalizedSort(request);
        Sort.Direction direction = "-timestamp".equals(sortValue) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = new OffsetLimitPageRequest(offset, limit, Sort.by(direction, "readingTimestamp").and(Sort.by(direction, "id")));
        Page<TemperatureReadingEntity> page = repository.findAll(specification(request), pageable);
        List<TemperatureReadingResponse> items = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();
        PaginationResponse pagination = new PaginationResponse(limit, offset, items.size(), page.getTotalElements());
        return new TemperatureReadingQueryResponse(items, pagination);
    }

    private Specification<TemperatureReadingEntity> specification(TemperatureReadingQueryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("readingTimestamp"), request.getFrom()));
            predicates.add(criteriaBuilder.lessThan(root.get("readingTimestamp"), request.getTo()));
            if (request.getSourceId() != null && !request.getSourceId().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("sourceId"), request.getSourceId()));
            }
            if (request.getUnit() != null && !request.getUnit().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("unit"), request.getUnit()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
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
            int previousOffset = Math.max(offset - limit, 0);
            return new OffsetLimitPageRequest(previousOffset, limit, sort);
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
