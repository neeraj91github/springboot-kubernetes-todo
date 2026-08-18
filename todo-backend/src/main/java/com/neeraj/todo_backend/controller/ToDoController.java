package com.neeraj.todo_backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.neeraj.todo_backend.dto.CustomUserPrincipal;
import com.neeraj.todo_backend.model.ToDo;
import com.neeraj.todo_backend.service.ToDoService;

@RestController
@RequestMapping("/api/todos")
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping
    public ResponseEntity<ToDo> createToDo(@AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody ToDo toDo){
        ToDo createdToDo = toDoService.createTodo(principal.getUserId(), toDo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdToDo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDo> getToDo(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long id){
        Optional<ToDo> toDo = toDoService.getTodo(id);
        if (toDo.isPresent() && toDo.get().getUserId().equals(principal.getUserId())){
            return ResponseEntity.ok(toDo.get());
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ToDo>> getToDos(@AuthenticationPrincipal CustomUserPrincipal principal){
        List<ToDo> toDoList = toDoService.getTodosForUser(principal.getUserId());
        return ResponseEntity.ok(toDoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDo> updateToDo(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long id, @RequestBody ToDo toDo){
        Optional<ToDo> currentToDo = toDoService.getTodo(id);
        if (currentToDo.isPresent()) {
            if (currentToDo.get().getUserId().equals(principal.getUserId())){
                toDo = toDoService.updateTodo(principal.getUserId(), currentToDo.get().getTodoId(), toDo);
                return ResponseEntity.ok(toDo);
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteToDo(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long id){
        Optional<ToDo> currentToDo = toDoService.getTodo(id);
        if (currentToDo.isPresent()) {
            if (currentToDo.get().getUserId().equals(principal.getUserId())){
                toDoService.deleteTodo(principal.getUserId(), id);
                return ResponseEntity.noContent().build();
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
