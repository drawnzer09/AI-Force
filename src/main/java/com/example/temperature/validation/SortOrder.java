package com.example.temperature.validation;

public enum SortOrder {
    TIMESTAMP_ASC("timestamp:asc"),
    TIMESTAMP_DESC("timestamp:desc");

    private final String apiValue;

    SortOrder(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }
}
