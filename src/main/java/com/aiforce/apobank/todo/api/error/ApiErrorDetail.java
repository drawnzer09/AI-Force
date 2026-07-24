package com.aiforce.apobank.todo.api.error;

public record ApiErrorDetail(
        String field,
        String issue
) {
}
