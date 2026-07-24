package com.aiforce.apobank.todo.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Todo {

    private final String id;
    private final String subject;
    private final TodoUrgency urgency;
    private TodoStatus status;
    private final String submitterName;
    private final Instant submissionTime;
    private final List<TodoStatusChange> statusChanges;

    public Todo(String id,
                String subject,
                TodoUrgency urgency,
                TodoStatus status,
                String submitterName,
                Instant submissionTime,
                List<TodoStatusChange> statusChanges) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.subject = Objects.requireNonNull(subject, "subject must not be null");
        this.urgency = Objects.requireNonNull(urgency, "urgency must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.submitterName = Objects.requireNonNull(submitterName, "submitterName must not be null");
        this.submissionTime = Objects.requireNonNull(submissionTime, "submissionTime must not be null");
        this.statusChanges = new ArrayList<>(Objects.requireNonNull(statusChanges, "statusChanges must not be null"));
    }

    public static Todo create(String id, String subject, TodoUrgency urgency, String submitterName, Instant submissionTime) {
        return new Todo(id, subject, urgency, TodoStatus.OPEN, submitterName, submissionTime, List.of());
    }

    public Todo copy() {
        return new Todo(id, subject, urgency, status, submitterName, submissionTime, statusChanges);
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public TodoUrgency getUrgency() {
        return urgency;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public Instant getSubmissionTime() {
        return submissionTime;
    }

    public List<TodoStatusChange> getStatusChanges() {
        return Collections.unmodifiableList(statusChanges);
    }

    public void applyStatusChange(TodoStatusChange statusChange) {
        Objects.requireNonNull(statusChange, "statusChange must not be null");
        this.status = statusChange.getNewStatus();
        this.statusChanges.add(statusChange);
    }
}
