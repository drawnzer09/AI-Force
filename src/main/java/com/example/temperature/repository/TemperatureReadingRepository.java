package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureReadingEntity;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemperatureReadingRepository extends JpaRepository<TemperatureReadingEntity, Long>, JpaSpecificationExecutor<TemperatureReadingEntity> {

    Page<TemperatureReadingEntity> findByReadingTimestampGreaterThanEqualAndReadingTimestampLessThan(
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    );
}
