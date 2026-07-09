package com.example.temperature.dto.response;

public class PaginationResponse {

    private int limit;
    private int offset;
    private int count;
    private long total;

    public PaginationResponse(int limit, int offset, int count, long total) {
        this.limit = limit;
        this.offset = offset;
        this.count = count;
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
