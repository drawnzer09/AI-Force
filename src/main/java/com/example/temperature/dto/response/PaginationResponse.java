package com.example.temperature.dto.response;

public class PaginationResponse {

    private int page;
    private int pageSize;
    private int returnedCount;
    private boolean hasNextPage;

    public PaginationResponse() {
    }

    public PaginationResponse(int page, int pageSize, int returnedCount, boolean hasNextPage) {
        this.page = page;
        this.pageSize = pageSize;
        this.returnedCount = returnedCount;
        this.hasNextPage = hasNextPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getReturnedCount() {
        return returnedCount;
    }

    public void setReturnedCount(int returnedCount) {
        this.returnedCount = returnedCount;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
