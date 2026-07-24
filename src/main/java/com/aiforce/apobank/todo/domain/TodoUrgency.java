package com.aiforce.apobank.todo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TodoUrgency {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String value;

    TodoUrgency(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TodoUrgency fromValue(String value) {
        return Arrays.stream(values())
                .filter(urgency -> urgency.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid urgency: " + value));
    }
}
