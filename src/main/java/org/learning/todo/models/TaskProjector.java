package org.learning.todo.models;

@FunctionalInterface
public interface TaskProjector<T> {
    T project(String id, String title, boolean isDone);
}
