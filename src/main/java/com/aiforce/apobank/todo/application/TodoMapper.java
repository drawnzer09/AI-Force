package com.aiforce.apobank.todo.application;

import com.aiforce.apobank.todo.api.dto.PageMetadataResponse;
import com.aiforce.apobank.todo.api.dto.StatusChangeResponse;
import com.aiforce.apobank.todo.api.dto.StatusChangeResultResponse;
import com.aiforce.apobank.todo.api.dto.TodoDetailResponse;
import com.aiforce.apobank.todo.api.dto.TodoListItemResponse;
import com.aiforce.apobank.todo.api.dto.TodoListResponse;
import com.aiforce.apobank.todo.domain.Todo;
import com.aiforce.apobank.todo.domain.TodoStatusChange;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoMapper {

    public TodoDetailResponse toDetailResponse(Todo todo) {
        return new TodoDetailResponse(
                todo.getId(),
                todo.getSubject(),
                todo.getUrgency(),
                todo.getStatus(),
                todo.getSubmitterName(),
                todo.getSubmissionTime(),
                todo.getStatusChanges().stream().map(this::toStatusChangeResponse).toList()
        );
    }

    public TodoListItemResponse toListItemResponse(Todo todo) {
        return new TodoListItemResponse(
                todo.getId(),
                todo.getSubject(),
                todo.getUrgency(),
                todo.getStatus(),
                todo.getSubmitterName(),
                todo.getSubmissionTime()
        );
    }

    public TodoListResponse toListResponse(List<Todo> todos, int page, int size, long totalItems) {
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / size);
        return new TodoListResponse(
                todos.stream().map(this::toListItemResponse).toList(),
                new PageMetadataResponse(page, size, totalItems, totalPages)
        );
    }

    public StatusChangeResponse toStatusChangeResponse(TodoStatusChange statusChange) {
        return new StatusChangeResponse(
                statusChange.getOldStatus(),
                statusChange.getNewStatus(),
                statusChange.getChangeSubmitter(),
                statusChange.getChangeTime()
        );
    }

    public StatusChangeResultResponse toStatusChangeResultResponse(Todo todo, TodoStatusChange statusChange) {
        return new StatusChangeResultResponse(todo.getId(), todo.getStatus(), toStatusChangeResponse(statusChange));
    }
}
