package com.example.temperature.validation;

import com.example.temperature.dto.request.TemperatureReadingQueryRequest;
import com.example.temperature.exception.InvalidQueryParameterException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TimeRangeValidator {

    public static final int DEFAULT_LIMIT = 100;
    public static final int MAX_LIMIT = 1000;
    public static final int DEFAULT_OFFSET = 0;
    public static final String DEFAULT_SORT = "timestamp";

    public void validate(TemperatureReadingQueryRequest request) {
        List<InvalidQueryParameterException.FieldIssue> issues = new ArrayList<>();
        if (request.getFrom() == null) {
            issues.add(new InvalidQueryParameterException.FieldIssue("from", "from is required"));
        }
        if (request.getTo() == null) {
            issues.add(new InvalidQueryParameterException.FieldIssue("to", "to is required"));
        }
        if (request.getFrom() != null && request.getTo() != null && !request.getFrom().isBefore(request.getTo())) {
            issues.add(new InvalidQueryParameterException.FieldIssue("from", "from must be earlier than to"));
        }
        if (request.getSourceId() != null && request.getSourceId().isBlank()) {
            issues.add(new InvalidQueryParameterException.FieldIssue("sourceId", "sourceId must not be blank"));
        }
        if (request.getSourceId() != null && request.getSourceId().length() > 128) {
            issues.add(new InvalidQueryParameterException.FieldIssue("sourceId", "sourceId must be at most 128 characters"));
        }
        if (request.getUnit() != null && !TemperatureUnit.isValid(request.getUnit())) {
            issues.add(new InvalidQueryParameterException.FieldIssue("unit", "unit must be one of C, F, K"));
        }
        int limit = normalizedLimit(request);
        int offset = normalizedOffset(request);
        if (limit < 1 || limit > MAX_LIMIT) {
            issues.add(new InvalidQueryParameterException.FieldIssue("limit", "limit must be between 1 and 1000"));
        }
        if (offset < 0) {
            issues.add(new InvalidQueryParameterException.FieldIssue("offset", "offset must be greater than or equal to 0"));
        }
        String sort = normalizedSort(request);
        if (!DEFAULT_SORT.equals(sort) && !"-timestamp".equals(sort)) {
            issues.add(new InvalidQueryParameterException.FieldIssue("sort", "sort must be timestamp or -timestamp"));
        }
        if (!issues.isEmpty()) {
            throw new InvalidQueryParameterException("Invalid query parameters", issues);
        }
    }

    public int normalizedLimit(TemperatureReadingQueryRequest request) {
        return request.getLimit() == null ? DEFAULT_LIMIT : request.getLimit();
    }

    public int normalizedOffset(TemperatureReadingQueryRequest request) {
        return request.getOffset() == null ? DEFAULT_OFFSET : request.getOffset();
    }

    public String normalizedSort(TemperatureReadingQueryRequest request) {
        return request.getSort() == null || request.getSort().isBlank() ? DEFAULT_SORT : request.getSort();
    }
}
