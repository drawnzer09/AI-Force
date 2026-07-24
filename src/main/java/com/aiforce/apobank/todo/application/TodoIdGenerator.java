package com.aiforce.apobank.todo.application;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class TodoIdGenerator {

    private final AtomicLong sequence = new AtomicLong(1000);

    public String nextId() {
        return "todo-" + sequence.incrementAndGet();
    }
}
