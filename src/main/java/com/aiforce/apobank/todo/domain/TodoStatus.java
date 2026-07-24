package com.aiforce.apobank.todo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TodoStatus {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    CLOSED("Closed");

    private final String value;

    TodoStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TodoStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + value));
    }
}
