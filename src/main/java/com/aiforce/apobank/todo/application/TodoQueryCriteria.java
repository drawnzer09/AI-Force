package com.aiforce.apobank.todo.application;

import com.aiforce.apobank.todo.domain.TodoStatus;
import com.aiforce.apobank.todo.domain.TodoUrgency;

import java.time.Instant;

public record TodoQueryCriteria(
        TodoUrgency urgency,
        TodoStatus status,
        Instant submittedFrom,
        Instant submittedTo,
        String submitterName,
        int page,
        int size,
        String sortField,
        String sortDirection
) {
}
