package com.example.temperature.exception;

public class InvalidQueryException extends RuntimeException {

    private final String field;
    private final String issue;

    public InvalidQueryException(String field, String issue) {
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
