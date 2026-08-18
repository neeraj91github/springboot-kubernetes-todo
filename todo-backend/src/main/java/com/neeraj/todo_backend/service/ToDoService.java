package com.neeraj.todo_backend.service;

import com.neeraj.todo_backend.model.ToDo;

import java.util.List;
import java.util.Optional;

public interface ToDoService {

    ToDo createTodo(Long userId, ToDo todo);

    List<ToDo> getTodosForUser(Long userId);

    Optional<ToDo> getTodo(Long todoId);

    ToDo updateTodo(Long userId, Long todoId, ToDo updatedTodo);

    void deleteTodo(Long userId, Long todoId);
}
