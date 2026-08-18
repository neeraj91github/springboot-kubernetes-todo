package com.neeraj.todo_backend.repository;

import com.neeraj.todo_backend.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    List<ToDo> findByUserId(Long userId);

    Optional<ToDo> findByTodoIdAndUserId(Long id, Long userId);
}
