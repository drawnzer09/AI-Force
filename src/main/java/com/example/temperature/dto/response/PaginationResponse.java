package com.example.temperature.dto.response;

public class PaginationResponse {

    private int limit;
    private int offset;
    private int count;

    public PaginationResponse() {
    }

    public PaginationResponse(int limit, int offset, int count) {
        this.limit = limit;
        this.offset = offset;
        this.count = count;
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
}
