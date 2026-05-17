package org.learning.todo.controller;

import org.junit.jupiter.api.Test;
import org.learning.todo.exceptions.TodoNotFoundException;
import org.learning.todo.service.TodoService;
import org.learning.todo.views.TodoView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureRestTestClient
class TodoControllerTest {
    @Autowired
    private RestTestClient client;

    @MockitoBean
    private TodoService todoService;

    @Test
    void shouldRetrieveTodo() throws TodoNotFoundException {
        TodoView expectedTodoView = new TodoView("2", "Official", List.of());
        when(todoService.getTodo("2")).thenReturn(expectedTodoView);

        TodoView actualTodoView = client.get().uri("/api/todo/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoView.class).returnResult().getResponseBody();

        assertEquals(expectedTodoView, actualTodoView);
    }
}