package com.aiforce.apobank.todo.application;

import com.aiforce.apobank.todo.api.dto.ChangeTodoStatusRequest;
import com.aiforce.apobank.todo.api.dto.CreateTodoRequest;
import com.aiforce.apobank.todo.api.dto.StatusChangeResultResponse;
import com.aiforce.apobank.todo.api.dto.TodoDetailResponse;
import com.aiforce.apobank.todo.api.dto.TodoListResponse;
import com.aiforce.apobank.todo.domain.Todo;
import com.aiforce.apobank.todo.domain.TodoStatusChange;
import com.aiforce.apobank.todo.exception.InvalidQueryParameterException;
import com.aiforce.apobank.todo.exception.TodoNotFoundException;
import com.aiforce.apobank.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final TodoIdGenerator todoIdGenerator;
    private final StatusTransitionPolicy statusTransitionPolicy;
    private final Clock clock;

    public TodoService(TodoRepository todoRepository,
                       TodoMapper todoMapper,
                       TodoIdGenerator todoIdGenerator,
                       StatusTransitionPolicy statusTransitionPolicy,
                       Clock clock) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
        this.todoIdGenerator = todoIdGenerator;
        this.statusTransitionPolicy = statusTransitionPolicy;
        this.clock = clock;
    }

    public TodoDetailResponse createTodo(CreateTodoRequest request) {
        Todo todo = Todo.create(
                todoIdGenerator.nextId(),
                request.subject().trim(),
                request.urgency(),
                request.submitterName().trim(),
                Instant.now(clock)
        );
        return todoMapper.toDetailResponse(todoRepository.save(todo));
    }

    public TodoDetailResponse getTodo(String todoId) {
        return todoRepository.findById(todoId)
                .map(todoMapper::toDetailResponse)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
    }

    public TodoListResponse listTodos(TodoQueryCriteria criteria) {
        validateCriteria(criteria);
        long totalItems = todoRepository.count(criteria);
        List<Todo> todos = todoRepository.findAll(criteria);
        return todoMapper.toListResponse(todos, criteria.page(), criteria.size(), totalItems);
    }

    public StatusChangeResultResponse changeStatus(String todoId, ChangeTodoStatusRequest request) {
        Todo existing = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));

        statusTransitionPolicy.validate(existing.getStatus(), request.newStatus());

        TodoStatusChange statusChange = new TodoStatusChange(
                existing.getStatus(),
                request.newStatus(),
                request.changeSubmitter().trim(),
                Instant.now(clock)
        );

        Todo updated = todoRepository.applyStatusChange(todoId, statusChange)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
        return todoMapper.toStatusChangeResultResponse(updated, statusChange);
    }

    private void validateCriteria(TodoQueryCriteria criteria) {
        if (criteria.submittedFrom() != null
                && criteria.submittedTo() != null
                && criteria.submittedFrom().isAfter(criteria.submittedTo())) {
            throw new InvalidQueryParameterException("submittedFrom", "submittedFrom must not be later than submittedTo");
        }
    }
}
