package com.aiforce.apobank.todo.api.dto;

import com.aiforce.apobank.todo.domain.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeTodoStatusRequest(
        @NotNull(message = "newStatus is required")
        TodoStatus newStatus,

        @NotBlank(message = "changeSubmitter is required")
        @Size(max = 100, message = "changeSubmitter must be at most 100 characters")
        String changeSubmitter
) {
}
