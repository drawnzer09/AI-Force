package com.example.temperature.dto.query;

import java.time.OffsetDateTime;

public class TemperatureRecordQuery {

    public static final int DEFAULT_LIMIT = 100;
    public static final int MAX_LIMIT = 1000;
    public static final int DEFAULT_OFFSET = 0;
    public static final String DEFAULT_SORT = "timestamp";

    private final OffsetDateTime startTimestamp;
    private final OffsetDateTime endTimestamp;
    private final int limit;
    private final int offset;
    private final String sort;

    public TemperatureRecordQuery(
            OffsetDateTime startTimestamp,
            OffsetDateTime endTimestamp,
            Integer limit,
            Integer offset,
            String sort
    ) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.limit = limit == null ? DEFAULT_LIMIT : limit;
        this.offset = offset == null ? DEFAULT_OFFSET : offset;
        this.sort = sort == null || sort.isBlank() ? DEFAULT_SORT : sort;
    }

    public OffsetDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public OffsetDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public String getSort() {
        return sort;
    }

    public boolean isDescending() {
        return "-timestamp".equals(sort);
    }
}
