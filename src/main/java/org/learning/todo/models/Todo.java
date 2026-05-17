package org.learning.todo.models;

import org.learning.todo.exceptions.TaskNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Todo {
    private final String id;
    private final Map<String, Task> tasks;
    private final String title;

    public Todo(String id, String title) {
        this.id = id;
        this.title = title;
        tasks = new HashMap<>();
    }

    public <T> T toggleStatus(String taskId, TaskProjector<T> projector) {
        Task task = this.tasks.get(taskId);
        if (task == null) throw new TaskNotFoundException(taskId);
        task.toggleStatus();
        return task.project(projector);
    }

    public <T, S> T project(TodoProjector<T, S> todoProjector, TaskProjector<S> taskProjector) {
        List<S> taskProjections = this.tasks.values().stream()
                .map(task -> task.project(taskProjector))
                .toList();
        return todoProjector.project(this.id, this.title, taskProjections);
    }

    public void addTask(Task task) {
        this.tasks.put(task.getId(), task);
    }
}
