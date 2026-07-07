package com.example.temperature.validation;

import com.example.temperature.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TemperatureQueryValidator {

    public static final int DEFAULT_LIMIT = 100;
    public static final int MAX_LIMIT = 1000;
    public static final int DEFAULT_OFFSET = 0;
    public static final String DEFAULT_SORT = "timestamp";

    public ValidatedQuery validate(OffsetDateTime startTime, OffsetDateTime endTime, Integer limit, Integer offset, String sort) {
        List<BadRequestException.FieldError> errors = new ArrayList<>();

        int resolvedLimit = limit == null ? DEFAULT_LIMIT : limit;
        int resolvedOffset = offset == null ? DEFAULT_OFFSET : offset;
        String resolvedSort = sort == null || sort.isBlank() ? DEFAULT_SORT : sort;

        if (resolvedLimit < 1 || resolvedLimit > MAX_LIMIT) {
            errors.add(new BadRequestException.FieldError("limit", "limit must be between 1 and 1000"));
        }
        if (resolvedOffset < 0) {
            errors.add(new BadRequestException.FieldError("offset", "offset must be greater than or equal to 0"));
        }
        if (!DEFAULT_SORT.equals(resolvedSort) && !"-timestamp".equals(resolvedSort)) {
            errors.add(new BadRequestException.FieldError("sort", "sort must be timestamp or -timestamp"));
        }
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            errors.add(new BadRequestException.FieldError("startTime", "startTime must be less than or equal to endTime"));
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid query parameters", errors);
        }

        return new ValidatedQuery(startTime, endTime, resolvedLimit, resolvedOffset, resolvedSort);
    }

    public record ValidatedQuery(
            OffsetDateTime startTime,
            OffsetDateTime endTime,
            int limit,
            int offset,
            String sort
    ) {
    }
}
