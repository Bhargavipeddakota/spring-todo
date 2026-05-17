package org.learning.todo.controller;

import org.learning.todo.exceptions.TaskNotFoundException;
import org.learning.todo.exceptions.TodoNotFoundException;
import org.learning.todo.service.TodoService;
import org.learning.todo.views.TaskCreationRequest;
import org.learning.todo.views.TaskView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo/{todoId}/task")
public class TaskController {
    private final TodoService todoService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TaskView> createTask(@PathVariable String todoId, @RequestBody TaskCreationRequest taskCreationRequest) {
        logger.info("Received request to create task in todo of id {}", todoId);
        try {
            TaskView task = this.todoService.createTask(todoId, taskCreationRequest.title());
            logger.info("Successfully create task in todo of id {}, newly created task id {}", todoId, task.id());
            return ResponseEntity.ok(task);
        } catch (TodoNotFoundException e) {
            logger.error("Failed to create task in todo of id {}, todo not found", todoId);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{taskId}/toggleStatus")
    public ResponseEntity<TaskView> toggleTaskStatus(@PathVariable String todoId, @PathVariable String taskId) {
        logger.info("Received request to toggle status in todo {}, task {}", todoId, taskId);
        try {
            TaskView taskView = this.todoService.toggleStatus(todoId, taskId);
            return ResponseEntity.ok(taskView);
        } catch (TodoNotFoundException e) {
            logger.error("Request to toggle status in todo {}, task {} failed todo not found", todoId, taskId);
            return ResponseEntity.notFound().build();
        } catch (TaskNotFoundException e) {
            logger.error("Request to toggle status in todo {}, task {} failed task not found", todoId, taskId);
            return ResponseEntity.notFound().build();
        }
    }
}
