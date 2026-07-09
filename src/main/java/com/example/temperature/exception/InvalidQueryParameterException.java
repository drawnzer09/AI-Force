package com.example.temperature.exception;

import java.util.List;

public class InvalidQueryParameterException extends RuntimeException {

    private final List<FieldIssue> fieldIssues;

    public InvalidQueryParameterException(String message, List<FieldIssue> fieldIssues) {
        super(message);
        this.fieldIssues = fieldIssues == null ? List.of() : List.copyOf(fieldIssues);
    }

    public List<FieldIssue> getFieldIssues() {
        return fieldIssues;
    }

    public record FieldIssue(String field, String issue) {
    }
}
