package com.aiforce.apobank.todo.exception;

public class InvalidQueryParameterException extends RuntimeException {

    private final String field;

    public InvalidQueryParameterException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
