package com.example.temperature.validation;

import java.time.OffsetDateTime;

public record ValidatedQuery(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        int limit,
        int offset,
        SortOrder sortOrder
) {
}
