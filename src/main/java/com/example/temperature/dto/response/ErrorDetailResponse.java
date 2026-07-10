package com.example.temperature.dto.response;

public class ErrorDetailResponse {

    private String field;
    private String issue;

    public ErrorDetailResponse() {
    }

    public ErrorDetailResponse(String field, String issue) {
        this.field = field;
        this.issue = issue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
