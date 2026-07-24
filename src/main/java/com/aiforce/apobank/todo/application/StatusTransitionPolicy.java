package com.aiforce.apobank.todo.application;

import com.aiforce.apobank.todo.domain.TodoStatus;
import com.aiforce.apobank.todo.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

@Component
public class StatusTransitionPolicy {

    public void validate(TodoStatus oldStatus, TodoStatus newStatus) {
        if (!isAllowed(oldStatus, newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Status transition from " + oldStatus.getValue() + " to " + newStatus.getValue() + " is not allowed");
        }
    }

    private boolean isAllowed(TodoStatus oldStatus, TodoStatus newStatus) {
        return (oldStatus == TodoStatus.OPEN && (newStatus == TodoStatus.IN_PROGRESS || newStatus == TodoStatus.CLOSED))
                || (oldStatus == TodoStatus.IN_PROGRESS && newStatus == TodoStatus.CLOSED);
    }
}
