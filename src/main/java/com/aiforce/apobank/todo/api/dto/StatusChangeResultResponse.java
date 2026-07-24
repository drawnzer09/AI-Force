package com.aiforce.apobank.todo.api.dto;

import com.aiforce.apobank.todo.domain.TodoStatus;

public record StatusChangeResultResponse(
        String todoId,
        TodoStatus status,
        StatusChangeResponse statusChange
) {
}
