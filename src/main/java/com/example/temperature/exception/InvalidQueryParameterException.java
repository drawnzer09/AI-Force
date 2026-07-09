package com.example.temperature.exception;

import java.util.List;

public class InvalidQueryParameterException extends RuntimeException {

    private final List<FieldIssue> issues;

    public InvalidQueryParameterException(String message, List<FieldIssue> issues) {
        super(message);
        this.issues = List.copyOf(issues);
    }

    public List<FieldIssue> getIssues() {
        return issues;
    }

    public record FieldIssue(String field, String issue) {
    }
}
