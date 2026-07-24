package com.aiforce.apobank.todo.domain;

import java.time.Instant;
import java.util.Objects;

public class TodoStatusChange {

    private final TodoStatus oldStatus;
    private final TodoStatus newStatus;
    private final String changeSubmitter;
    private final Instant changeTime;

    public TodoStatusChange(TodoStatus oldStatus, TodoStatus newStatus, String changeSubmitter, Instant changeTime) {
        this.oldStatus = Objects.requireNonNull(oldStatus, "oldStatus must not be null");
        this.newStatus = Objects.requireNonNull(newStatus, "newStatus must not be null");
        this.changeSubmitter = Objects.requireNonNull(changeSubmitter, "changeSubmitter must not be null");
        this.changeTime = Objects.requireNonNull(changeTime, "changeTime must not be null");
    }

    public TodoStatus getOldStatus() {
        return oldStatus;
    }

    public TodoStatus getNewStatus() {
        return newStatus;
    }

    public String getChangeSubmitter() {
        return changeSubmitter;
    }

    public Instant getChangeTime() {
        return changeTime;
    }
}
