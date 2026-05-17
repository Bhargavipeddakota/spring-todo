package org.learning.todo.exceptions;

public class TodoNotFoundException extends TodoException {
    private final String todoId;

    public TodoNotFoundException(String todoId) {
        super("Todo Not Found: " + todoId);
        this.todoId = todoId;
    }
}
