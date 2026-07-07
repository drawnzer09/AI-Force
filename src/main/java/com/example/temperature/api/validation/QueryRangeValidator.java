package com.example.temperature.api.validation;

import com.example.temperature.exception.InvalidQueryRangeException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class QueryRangeValidator {

    public void validate(OffsetDateTime startTimestamp, OffsetDateTime endTimestamp) {
        if (startTimestamp != null
                && endTimestamp != null
                && startTimestamp.toInstant().isAfter(endTimestamp.toInstant())) {
            throw new InvalidQueryRangeException("startTimestamp must be less than or equal to endTimestamp");
        }
    }
}
