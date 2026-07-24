package com.aiforce.apobank.todo.api.dto;

import java.util.List;

public record TodoListResponse(
        List<TodoListItemResponse> items,
        PageMetadataResponse page
) {
}
