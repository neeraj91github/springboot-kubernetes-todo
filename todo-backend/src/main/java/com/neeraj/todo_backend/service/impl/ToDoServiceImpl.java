package com.neeraj.todo_backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.neeraj.todo_backend.model.ToDo;
import com.neeraj.todo_backend.repository.ToDoRepository;
import com.neeraj.todo_backend.service.ToDoService;

@Service
public class ToDoServiceImpl implements ToDoService {

    private ToDoRepository repository;

    public ToDoServiceImpl(ToDoRepository repository) {
        this.repository = repository;
    }

    @Override
    public ToDo createTodo(Long userId, ToDo todo) {
        todo.setUserId(userId);
        return repository.save(todo);
    }

    @Override
    public List<ToDo> getTodosForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Optional<ToDo> getTodo(Long todoId) {
        return repository.findById(todoId);
    }

    @Override
    public ToDo updateTodo(Long userId, Long todoId, ToDo updatedTodo) {
        ToDo toDo = repository.findByTodoIdAndUserId(todoId, userId).orElseThrow(() -> new RuntimeException("ToDo item not found."));
        toDo.setTitle(updatedTodo.getTitle());
        toDo.setCompleted(updatedTodo.isCompleted());
        toDo.setDescription(updatedTodo.getDescription());
        toDo.setDueDate(updatedTodo.getDueDate());
        return repository.save(toDo);
    }

    @Override
    public void deleteTodo(Long userId, Long todoId) {
        ToDo toDo = repository.findById(todoId).orElseThrow(() -> new RuntimeException("ToDo item not found."));
        repository.delete(toDo);
    }
}
