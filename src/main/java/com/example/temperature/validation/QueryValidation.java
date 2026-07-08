package com.example.temperature.validation;

import com.example.temperature.dto.TemperatureQueryRequest;
import com.example.temperature.exception.ApiException;
import com.example.temperature.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class QueryValidation {

    public static final int DEFAULT_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;
    public static final String DEFAULT_SORT = "timestamp:asc";

    public ValidatedQuery validate(TemperatureQueryRequest request) {
        if (request.endTime().isBefore(request.startTime())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "endTime must be greater than or equal to startTime", "endTime");
        }

        int limit = request.limit() == null ? DEFAULT_LIMIT : request.limit();
        int offset = request.offset() == null ? DEFAULT_OFFSET : request.offset();
        SortOrder sortOrder = parseSort(request.sort() == null || request.sort().isBlank() ? DEFAULT_SORT : request.sort());

        return new ValidatedQuery(request.startTime(), request.endTime(), limit, offset, sortOrder);
    }

    private SortOrder parseSort(String sort) {
        if (SortOrder.TIMESTAMP_ASC.getApiValue().equals(sort)) {
            return SortOrder.TIMESTAMP_ASC;
        }
        if (SortOrder.TIMESTAMP_DESC.getApiValue().equals(sort)) {
            return SortOrder.TIMESTAMP_DESC;
        }
        throw new ApiException(ErrorCode.INVALID_REQUEST, "sort must be either timestamp:asc or timestamp:desc", "sort");
    }
}
