package com.aiforce.apobank.todo.api;

import com.aiforce.apobank.todo.api.dto.ChangeTodoStatusRequest;
import com.aiforce.apobank.todo.api.dto.CreateTodoRequest;
import com.aiforce.apobank.todo.api.dto.StatusChangeResultResponse;
import com.aiforce.apobank.todo.api.dto.TodoDetailResponse;
import com.aiforce.apobank.todo.api.dto.TodoListResponse;
import com.aiforce.apobank.todo.application.TodoQueryCriteria;
import com.aiforce.apobank.todo.application.TodoService;
import com.aiforce.apobank.todo.domain.TodoStatus;
import com.aiforce.apobank.todo.domain.TodoUrgency;
import com.aiforce.apobank.todo.exception.InvalidQueryParameterException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/todos")
@Validated
public class TodoController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "subject", "urgency", "status", "submitterName", "submissionTime");

    private final TodoService todoService;
    private final int defaultPageSize;
    private final int maxPageSize;

    public TodoController(TodoService todoService,
                          @Value("${todo.pagination.default-size:50}") int defaultPageSize,
                          @Value("${todo.pagination.max-size:100}") int maxPageSize) {
        this.todoService = todoService;
        this.defaultPageSize = defaultPageSize;
        this.maxPageSize = maxPageSize;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoDetailResponse createTodo(@Valid @RequestBody CreateTodoRequest request) {
        return todoService.createTodo(request);
    }

    @GetMapping
    public TodoListResponse listTodos(
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant submittedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant submittedTo,
            @RequestParam(required = false) String submitterName,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page must be greater than or equal to 0") int page,
            @RequestParam(required = false) @Min(value = 1, message = "size must be greater than or equal to 1") @Max(value = 100, message = "size must be less than or equal to 100") Integer size,
            @RequestParam(defaultValue = "submissionTime,desc") String sort) {
        int resolvedSize = size == null ? defaultPageSize : size;
        if (resolvedSize > maxPageSize) {
            throw new InvalidQueryParameterException("size", "size must be less than or equal to " + maxPageSize);
        }
        SortParts sortParts = parseSort(sort);
        return todoService.listTodos(new TodoQueryCriteria(
                parseUrgency(urgency),
                parseStatus(status),
                submittedFrom,
                submittedTo,
                submitterName,
                page,
                resolvedSize,
                sortParts.field(),
                sortParts.direction()
        ));
    }

    @GetMapping("/{todoId}")
    public TodoDetailResponse getTodo(@PathVariable String todoId) {
        return todoService.getTodo(todoId);
    }

    @PostMapping("/{todoId}/status-changes")
    @ResponseStatus(HttpStatus.CREATED)
    public StatusChangeResultResponse changeStatus(@PathVariable String todoId,
                                                   @Valid @RequestBody ChangeTodoStatusRequest request) {
        return todoService.changeStatus(todoId, request);
    }

    private TodoUrgency parseUrgency(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return TodoUrgency.fromValue(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidQueryParameterException("urgency", "urgency must be one of: Low, Medium, High");
        }
    }

    private TodoStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return TodoStatus.fromValue(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidQueryParameterException("status", "status must be one of: Open, In Progress, Closed");
        }
    }

    private SortParts parseSort(String sort) {
        String[] parts = sort.split(",");
        if (parts.length != 2) {
            throw new InvalidQueryParameterException("sort", "sort must use the format field,direction");
        }
        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new InvalidQueryParameterException("sort", "sort field must be one of: id, subject, urgency, status, submitterName, submissionTime");
        }
        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new InvalidQueryParameterException("sort", "sort direction must be asc or desc");
        }
        return new SortParts(field, direction);
    }

    private record SortParts(String field, String direction) {
    }
}
