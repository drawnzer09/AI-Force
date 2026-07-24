package com.aiforce.apobank.todo.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String todoId) {
        super("TODO not found: " + todoId);
    }
}
