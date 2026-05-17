package org.learning.todo.models;

public class Task {
    private final String title;
    private final String id;
    private boolean isDone;

    public Task(String id, String title, boolean isDone) {
        this.title = title;
        this.id = id;
        this.isDone = isDone;
    }

    public String getId() {
        return this.id;
    }

    public void toggleStatus() {
        this.isDone = !this.isDone;
    }

    public <T> T project(TaskProjector<T> projector) {
        return projector.project(this.id, this.title, this.isDone);
    }
}
