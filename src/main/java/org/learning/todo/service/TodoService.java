package org.learning.todo.service;

import org.jspecify.annotations.NonNull;
import org.learning.todo.exceptions.TodoNotFoundException;
import org.learning.todo.models.Task;
import org.learning.todo.models.Todo;
import org.learning.todo.views.TaskView;
import org.learning.todo.views.TodoView;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class TodoService {
    private final HashMap<String, Todo> todos;
    private final IdGenerator idGenerator;

    public TodoService(IdGenerator idGenerator) {
        this.todos = new HashMap<>();
        this.idGenerator = idGenerator;
    }

    public List<TodoView> listAll() {
        return this.todos.values().stream()
                .map(todo -> todo.project(TodoView::new, TaskView::new))
                .toList();
    }

    public TodoView createTodo(String title) {
        String id = this.idGenerator.generate();
        Todo todo = new Todo(id, title);
        this.todos.put(id, todo);
        return todo.project(TodoView::new, TaskView::new);
    }

    public TaskView toggleStatus(String todoId, String taskId) {
        return getRawTodo(todoId).toggleStatus(taskId, TaskView::new);
    }

    public TodoView getTodo(String todoId) {
        return getRawTodo(todoId).project(TodoView::new, TaskView::new);
    }

    private @NonNull Todo getRawTodo(String todoId) {
        Todo todo = this.todos.get(todoId);
        if (todo == null) throw new TodoNotFoundException(todoId);
        return todo;
    }

    public TaskView createTask(String todoId, String taskTitle) {
        Todo todo = this.getRawTodo(todoId);
        String taskId = this.idGenerator.generate();
        Task task = new Task(taskId, taskTitle, false);
        todo.addTask(task);
        return task.project(TaskView::new);
    }
}
