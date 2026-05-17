package org.learning.todo.views;

import java.util.List;

public record TodoView(String id, String title, List<TaskView> tasks) {
}
