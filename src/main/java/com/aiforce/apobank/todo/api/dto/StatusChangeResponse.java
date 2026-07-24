package com.aiforce.apobank.todo.api.dto;

import com.aiforce.apobank.todo.domain.TodoStatus;

import java.time.Instant;

public record StatusChangeResponse(
        TodoStatus oldStatus,
        TodoStatus newStatus,
        String changeSubmitter,
        Instant changeTime
) {
}
