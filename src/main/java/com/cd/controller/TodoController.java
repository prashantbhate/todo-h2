package com.cd.controller;

import com.cd.common.exceptions.TodoIDMismatchException;
import com.cd.model.Todo;
import com.cd.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    TodoService todoService;

    @GetMapping
    public ResponseEntity<List<Todo>> getTodos() {
        return ResponseEntity.ok(todoService.getTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable String id) {
        return ResponseEntity.ok(todoService.getTodoById(UUID.fromString(id)));
    }

    @PostMapping
    public ResponseEntity<Todo> addTodos(@Valid @RequestBody Todo todo) {
        Todo newTodo = todoService.addTodo(todo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newTodo.getId()).toUri();
        return ResponseEntity.created(location).body(newTodo);
    }

    @GetMapping("/users/{user}")
    public ResponseEntity<List<Todo>> getTodosByUser(@PathVariable String user) {
        return ResponseEntity.ok(todoService.getTodosByUser(user));
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable String id, @Valid @RequestBody Todo todo) {
        if (UUID.fromString(id).equals(todo.getId()))
            return todoService.updateTodo(todo);
        else
            throw new TodoIDMismatchException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTodo(@PathVariable String id) {
        if (todoService.deleteTodo(UUID.fromString(id))) {
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }

    @GetMapping("/target/{date}")
    public ResponseEntity<List<Todo>> getTodosByUser(@PathVariable LocalDate date) {
        return ResponseEntity.ok(todoService.getTodosBeforeTargetDate(date));
    }
}
