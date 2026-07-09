package com.example.temperature.validation;

import com.example.temperature.dto.request.TemperatureReadingRequest;
import com.example.temperature.exception.InvalidRequestException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TemperatureReadingValidator {

    private static final int MAX_BATCH_SIZE = 1000;
    private static final BigDecimal ABSOLUTE_ZERO_C = new BigDecimal("-273.15");
    private static final BigDecimal ABSOLUTE_ZERO_F = new BigDecimal("-459.67");
    private static final BigDecimal ABSOLUTE_ZERO_K = BigDecimal.ZERO;

    public void validate(List<TemperatureReadingRequest> readings) {
        List<InvalidRequestException.FieldIssue> issues = new ArrayList<>();
        if (readings == null) {
            issues.add(new InvalidRequestException.FieldIssue("readings", "readings is required"));
        } else {
            if (readings.size() > MAX_BATCH_SIZE) {
                throw new InvalidRequestException(
                        "Ingestion batch exceeds maximum item count",
                        List.of(new InvalidRequestException.FieldIssue("readings", "readings must contain at most 1000 items")),
                        true
                );
            }
            for (int i = 0; i < readings.size(); i++) {
                validateReading(readings.get(i), i, issues);
            }
        }
        if (!issues.isEmpty()) {
            throw new InvalidRequestException("Request validation failed", issues);
        }
    }

    private void validateReading(TemperatureReadingRequest reading, int index, List<InvalidRequestException.FieldIssue> issues) {
        String prefix = "readings[" + index + "]";
        if (reading == null) {
            issues.add(new InvalidRequestException.FieldIssue(prefix, "reading must not be null"));
            return;
        }
        if (reading.getTemperature() == null || reading.getUnit() == null) {
            return;
        }
        if (!TemperatureUnit.isValid(reading.getUnit())) {
            return;
        }
        BigDecimal minimum = switch (TemperatureUnit.valueOf(reading.getUnit())) {
            case C -> ABSOLUTE_ZERO_C;
            case F -> ABSOLUTE_ZERO_F;
            case K -> ABSOLUTE_ZERO_K;
        };
        if (reading.getTemperature().compareTo(minimum) < 0) {
            issues.add(new InvalidRequestException.FieldIssue(prefix + ".temperature", "temperature must be greater than or equal to absolute zero for unit " + reading.getUnit()));
        }
    }
}
