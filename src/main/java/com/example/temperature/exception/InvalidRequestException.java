package com.example.temperature.exception;

import java.util.List;

public class InvalidRequestException extends RuntimeException {

    private final List<FieldIssue> fieldIssues;
    private final boolean payloadTooLarge;

    public InvalidRequestException(String message, List<FieldIssue> fieldIssues) {
        this(message, fieldIssues, false);
    }

    public InvalidRequestException(String message, List<FieldIssue> fieldIssues, boolean payloadTooLarge) {
        super(message);
        this.fieldIssues = fieldIssues == null ? List.of() : List.copyOf(fieldIssues);
        this.payloadTooLarge = payloadTooLarge;
    }

    public List<FieldIssue> getFieldIssues() {
        return fieldIssues;
    }

    public boolean isPayloadTooLarge() {
        return payloadTooLarge;
    }

    public record FieldIssue(String field, String issue) {
    }
}
