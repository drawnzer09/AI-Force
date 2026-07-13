package com.example.temperature.repository;

import com.example.temperature.entity.TemperatureDataPointEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.OffsetDateTime;
import java.util.List;

public class TemperatureDataPointRepositoryImpl implements TemperatureDataPointRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TemperatureDataPointEntity> findByTimestampRange(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp,
            int limit,
            int offset
    ) {
        StringBuilder jpql = new StringBuilder("SELECT t FROM TemperatureDataPointEntity t WHERE 1 = 1");

        if (startTimestamp != null) {
            jpql.append(" AND t.timestamp >= :startTimestamp");
        }
        if (endTimestamp != null) {
            jpql.append(" AND t.timestamp <= :endTimestamp");
        }

        jpql.append(" ORDER BY t.timestamp ASC, t.id ASC");

        TypedQuery<TemperatureDataPointEntity> query =
                entityManager.createQuery(jpql.toString(), TemperatureDataPointEntity.class);

        if (startTimestamp != null) {
            query.setParameter("startTimestamp", startTimestamp);
        }
        if (endTimestamp != null) {
            query.setParameter("endTimestamp", endTimestamp);
        }

        return query
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
