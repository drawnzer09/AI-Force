package com.aiforce.apobank.todo.repository;

import com.aiforce.apobank.todo.application.TodoQueryCriteria;
import com.aiforce.apobank.todo.domain.Todo;
import com.aiforce.apobank.todo.domain.TodoStatusChange;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Repository
public class InMemoryTodoRepository implements TodoRepository {

    private final Map<String, Todo> todos = new ConcurrentHashMap<>();

    @Override
    public Todo save(Todo todo) {
        todos.put(todo.getId(), todo.copy());
        return todo.copy();
    }

    @Override
    public Optional<Todo> findById(String id) {
        Todo todo = todos.get(id);
        return todo == null ? Optional.empty() : Optional.of(todo.copy());
    }

    @Override
    public List<Todo> findAll(TodoQueryCriteria criteria) {
        return matchingTodos(criteria)
                .stream()
                .sorted(comparator(criteria))
                .skip((long) criteria.page() * criteria.size())
                .limit(criteria.size())
                .map(Todo::copy)
                .toList();
    }

    @Override
    public long count(TodoQueryCriteria criteria) {
        return matchingTodos(criteria).size();
    }

    @Override
    public Optional<Todo> applyStatusChange(String id, TodoStatusChange statusChange) {
        synchronized (todos) {
            Todo todo = todos.get(id);
            if (todo == null) {
                return Optional.empty();
            }
            todo.applyStatusChange(statusChange);
            return Optional.of(todo.copy());
        }
    }

    private List<Todo> matchingTodos(TodoQueryCriteria criteria) {
        Predicate<Todo> predicate = todo -> true;

        if (criteria.urgency() != null) {
            predicate = predicate.and(todo -> todo.getUrgency() == criteria.urgency());
        }
        if (criteria.status() != null) {
            predicate = predicate.and(todo -> todo.getStatus() == criteria.status());
        }
        if (criteria.submittedFrom() != null) {
            predicate = predicate.and(todo -> !todo.getSubmissionTime().isBefore(criteria.submittedFrom()));
        }
        if (criteria.submittedTo() != null) {
            predicate = predicate.and(todo -> !todo.getSubmissionTime().isAfter(criteria.submittedTo()));
        }
        if (criteria.submitterName() != null && !criteria.submitterName().isBlank()) {
            predicate = predicate.and(todo -> todo.getSubmitterName().equals(criteria.submitterName()));
        }

        return todos.values().stream()
                .map(Todo::copy)
                .filter(predicate)
                .toList();
    }

    private Comparator<Todo> comparator(TodoQueryCriteria criteria) {
        Comparator<Todo> comparator = switch (criteria.sortField()) {
            case "id" -> Comparator.comparing(Todo::getId);
            case "subject" -> Comparator.comparing(Todo::getSubject);
            case "urgency" -> Comparator.comparing(todo -> todo.getUrgency().getValue());
            case "status" -> Comparator.comparing(todo -> todo.getStatus().getValue());
            case "submitterName" -> Comparator.comparing(Todo::getSubmitterName);
            case "submissionTime" -> Comparator.comparing(Todo::getSubmissionTime);
            default -> Comparator.comparing(Todo::getSubmissionTime);
        };

        if ("desc".equalsIgnoreCase(criteria.sortDirection())) {
            comparator = comparator.reversed();
        }
        return comparator.thenComparing(Todo::getId);
    }
}
