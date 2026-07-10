package com.example.temperature.exception;

public class QueryValidationException extends RuntimeException {

    private final String field;
    private final String issue;

    public QueryValidationException(String field, String issue) {
        super(issue);
        this.field = field;
        this.issue = issue;
    }

    public String getField() {
        return field;
    }

    public String getIssue() {
        return issue;
    }
}
