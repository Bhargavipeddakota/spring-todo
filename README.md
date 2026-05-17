# Spring Concepts in This Project

This document explains the key Spring concepts used in this Todo application.

---

## 1. `@RestController`

`@RestController` marks a class as an HTTP request handler where every method automatically serializes the return value to JSON (or XML) and writes it directly into the HTTP response body. It is a composed annotation that combines `@Controller` and `@ResponseBody`.

**Used in this project:**

```java
// TodoController.java
@RestController
@RequestMapping("/api/todo")
public class TodoController { ... }

// TaskController.java
@RestController
@RequestMapping("/api/todo/{todoId}/task")
public class TaskController { ... }
```

**Documentation:** https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html

---

## 2. `@RequestMapping`, `@RequestBody`, and `@PathVariable`

### `@RequestMapping`

Maps HTTP requests to handler classes or methods. When placed on a class, it sets a base path for all methods in that class. Shorthand variants — `@GetMapping`, `@PostMapping`, `@PatchMapping`, etc. — combine `@RequestMapping` with a specific HTTP method.

```java
@RequestMapping("/api/todo")          // base path for all endpoints in TodoController
public class TodoController { ... }

@GetMapping("{todoId}")               // GET /api/todo/{todoId}
@PostMapping                          // POST /api/todo
@PatchMapping("/{taskId}/toggleStatus") // PATCH /api/todo/{todoId}/task/{taskId}/toggleStatus
```

**Documentation:** https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html

---

### `@RequestBody`

Binds the HTTP request body to a method parameter. Spring uses `Jackson` under the hood to deserialize the incoming JSON into the target Java object.

```java
// TodoController.java
@PostMapping
public ResponseEntity<TodoView> createTodo(@RequestBody TodoCreationRequest todoCreationRequest) {
    ...
}

// TaskController.java
@PostMapping
public ResponseEntity<TaskView> createTask(@PathVariable String todoId, @RequestBody TaskCreationRequest taskCreationRequest) {
    ...
}
```

**Documentation:** https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestBody.html

---

### `@PathVariable`

Extracts a value from the URI path and binds it to a method parameter. The placeholder in the `@RequestMapping` URL template (e.g., `{todoId}`) must match the parameter name (or the value specified in the annotation).

```java
// GET /api/todo/42  →  todoId = "42"
@GetMapping("{todoId}")
public ResponseEntity<TodoView> getTodo(@PathVariable String todoId) { ... }

// PATCH /api/todo/42/task/T7/toggleStatus  →  todoId = "42", taskId = "T7"
@PatchMapping("/{taskId}/toggleStatus")
public ResponseEntity<TaskView> toggleTaskStatus(
        @PathVariable String todoId,
        @PathVariable String taskId) { ... }
```

**Documentation:** https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/PathVariable.html

---

## 3. Logging and its Configuration

This project uses **SLF4J** as the logging facade, backed by **Logback** (the default provided by `spring-boot-starter`). SLF4J decouples application code from the underlying logging library — you can swap Logback for Log4j2 without changing any application code.

### Getting a logger

```java
// TodoController.java
private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
```

`LoggerFactory.getLogger(Class)` creates a logger whose name is the fully-qualified class name. This name is used in log output and for configuring per-class log levels.

### Logging in practice

```java
logger.info("Received request to find todo of id {}", todoId);   // normal flow
logger.error("Todo of id {} not found", todoId);                  // error path
```

The `{}` placeholders are filled lazily — the string is only formatted if the message will actually be logged, avoiding unnecessary work at disabled log levels.

### Configuration via `application.properties`

Spring Boot exposes logging configuration through `application.properties` without requiring a separate `logback.xml` for common cases:

```properties
# Set root log level
logging.level.root=WARN

# Set level for a specific package
logging.level.org.learning.todo=DEBUG

# Write logs to a file
logging.file.name=logs/todo.log

# Custom log pattern
logging.pattern.console=%d{HH:mm:ss} %-5level %logger{36} - %msg%n
```

**Documentation:**
- SLF4J: https://www.slf4j.org/manual.html
- Spring Boot Logging: https://docs.spring.io/spring-boot/reference/features/logging.html

---

## 4. `@MockitoBean`

`@MockitoBean` (introduced in Spring Boot 3.4, in the `org.springframework.test.context.bean.override.mockito` package) creates a Mockito mock and registers it as a Spring bean in the application context, replacing any existing bean of the same type for the duration of the test. This lets controller tests run against the real Spring MVC wiring while controlling the behaviour of service-layer dependencies.

```java
// TodoControllerTest.java
@SpringBootTest
@AutoConfigureRestTestClient
class TodoControllerTest {

    @Autowired
    private RestTestClient client;   // real HTTP client wired to the test server

    @MockitoBean
    private TodoService todoService; // Mockito mock, replaces the real TodoService bean

    @Test
    void shouldRetrieveTodo() throws TodoNotFoundException {
        TodoView expected = new TodoView("2", "Official", List.of());
        when(todoService.getTodo("2")).thenReturn(expected);  // stub with Mockito

        TodoView actual = client.get().uri("/api/todo/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TodoView.class).returnResult().getResponseBody();

        assertEquals(expected, actual);
    }
}
```

**Key points:**
- The mock is reset between tests automatically.
- Use `when(...).thenReturn(...)` to stub behaviour and `verify(...)` to assert interactions.
- Prefer `@MockitoBean` over `@MockBean` (the older Boot annotation) in Spring Boot 3.4+.

**Documentation:** https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/bean/override/mockito/MockitoBean.html
