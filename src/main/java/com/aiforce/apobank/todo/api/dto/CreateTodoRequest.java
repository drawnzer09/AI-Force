package com.aiforce.apobank.todo.api.dto;

import com.aiforce.apobank.todo.domain.TodoUrgency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTodoRequest(
        @NotBlank(message = "subject is required")
        @Size(max = 255, message = "subject must be at most 255 characters")
        String subject,

        @NotNull(message = "urgency is required")
        TodoUrgency urgency,

        @NotBlank(message = "submitterName is required")
        @Size(max = 100, message = "submitterName must be at most 100 characters")
        String submitterName
) {
}
