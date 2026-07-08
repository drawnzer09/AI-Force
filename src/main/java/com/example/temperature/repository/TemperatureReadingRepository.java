package com.example.temperature.repository;

import com.example.temperature.persistence.TemperatureReadingRow;

import java.time.OffsetDateTime;
import java.util.List;

public interface TemperatureReadingRepository {

    int saveAll(List<TemperatureReadingRow> rows);

    List<TemperatureReadingRow> findByTimestampRange(OffsetDateTime fromTimestamp,
                                                      OffsetDateTime toTimestamp,
                                                      int limit,
                                                      int offset);

    boolean isAvailable();
}
