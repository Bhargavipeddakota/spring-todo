package org.learning.todo.models;

import java.util.List;

@FunctionalInterface
public interface TodoProjector<T, S> {
    T project(String id, String title, List<S> tasks);
}
