package com.aiforce.apobank.todo.api.dto;

import com.aiforce.apobank.todo.domain.TodoStatus;
import com.aiforce.apobank.todo.domain.TodoUrgency;

import java.time.Instant;

public record TodoListItemResponse(
        String id,
        String subject,
        TodoUrgency urgency,
        TodoStatus status,
        String submitterName,
        Instant submissionTime
) {
}
