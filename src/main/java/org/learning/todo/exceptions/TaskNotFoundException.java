package org.learning.todo.exceptions;

public class TaskNotFoundException extends TodoException {
    private final String taskId;

    public TaskNotFoundException(String taskId) {
        super("Task of id %s not found".formatted(taskId));
        this.taskId = taskId;
    }
}
