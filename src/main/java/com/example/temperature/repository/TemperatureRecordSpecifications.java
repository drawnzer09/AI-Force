package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureRecordEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class TemperatureRecordSpecifications {

    private TemperatureRecordSpecifications() {
    }

    public static Specification<TemperatureRecordEntity> timestampAtOrAfter(OffsetDateTime startTimestamp) {
        return (root, query, criteriaBuilder) -> startTimestamp == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startTimestamp);
    }

    public static Specification<TemperatureRecordEntity> timestampAtOrBefore(OffsetDateTime endTimestamp) {
        return (root, query, criteriaBuilder) -> endTimestamp == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endTimestamp);
    }

    public static Specification<TemperatureRecordEntity> timestampBetween(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp
    ) {
        return Specification.where(timestampAtOrAfter(startTimestamp))
                .and(timestampAtOrBefore(endTimestamp));
    }
}
