package com.aiforce.apobank.todo.repository;

import com.aiforce.apobank.todo.application.TodoQueryCriteria;
import com.aiforce.apobank.todo.domain.Todo;
import com.aiforce.apobank.todo.domain.TodoStatusChange;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    Todo save(Todo todo);

    Optional<Todo> findById(String id);

    List<Todo> findAll(TodoQueryCriteria criteria);

    long count(TodoQueryCriteria criteria);

    Optional<Todo> applyStatusChange(String id, TodoStatusChange statusChange);
}
