package com.example.temperature.exception;

import java.util.List;

public class InvalidRequestException extends RuntimeException {

    private final List<FieldIssue> issues;
    private final boolean payloadTooLarge;

    public InvalidRequestException(String message, List<FieldIssue> issues) {
        this(message, issues, false);
    }

    public InvalidRequestException(String message, List<FieldIssue> issues, boolean payloadTooLarge) {
        super(message);
        this.issues = List.copyOf(issues);
        this.payloadTooLarge = payloadTooLarge;
    }

    public List<FieldIssue> getIssues() {
        return issues;
    }

    public boolean isPayloadTooLarge() {
        return payloadTooLarge;
    }

    public record FieldIssue(String field, String issue) {
    }
}
