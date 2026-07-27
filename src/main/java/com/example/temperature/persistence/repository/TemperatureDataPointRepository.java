package com.example.temperature.persistence.repository;

import com.example.temperature.persistence.entity.TemperatureDataPointEntity;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TemperatureDataPointRepository extends JpaRepository<TemperatureDataPointEntity, Long> {

    @Query(
            value = """
                    select dataPoint
                    from TemperatureDataPointEntity dataPoint
                    where (:fromTimestamp is null or dataPoint.recordedAt >= :fromTimestamp)
                      and (:toTimestamp is null or dataPoint.recordedAt <= :toTimestamp)
                    order by dataPoint.recordedAt asc, dataPoint.id asc
                    """,
            countQuery = """
                    select count(dataPoint)
                    from TemperatureDataPointEntity dataPoint
                    where (:fromTimestamp is null or dataPoint.recordedAt >= :fromTimestamp)
                      and (:toTimestamp is null or dataPoint.recordedAt <= :toTimestamp)
                    """
    )
    Page<TemperatureDataPointEntity> findByOptionalTimestampRange(
            @Param("fromTimestamp") OffsetDateTime fromTimestamp,
            @Param("toTimestamp") OffsetDateTime toTimestamp,
            Pageable pageable
    );
}
