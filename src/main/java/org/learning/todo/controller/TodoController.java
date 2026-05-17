package org.learning.todo.controller;

import org.learning.todo.exceptions.TodoNotFoundException;
import org.learning.todo.service.TodoService;
import org.learning.todo.views.TodoCreationRequest;
import org.learning.todo.views.TodoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("{todoId}")
    public ResponseEntity<TodoView> getTodo(@PathVariable String todoId) {
        logger.info("Received request to find todo of id {}", todoId);
        try {
            TodoView todoView = this.todoService.getTodo(todoId);
            logger.info("Responding with todo of id {}", todoId);
            return ResponseEntity.ok(todoView);
        } catch (TodoNotFoundException e) {
            logger.error("Todo of id {} not found", todoId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TodoView> createTodo(@RequestBody TodoCreationRequest todoCreationRequest) {
        logger.info("Received request to create todo");
        TodoView todoView = this.todoService.createTodo(todoCreationRequest.title());
        return ResponseEntity.ok(todoView);
    }
}
