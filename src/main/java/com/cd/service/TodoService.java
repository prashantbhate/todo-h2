package com.cd.service;

import com.cd.common.exceptions.TodoNotFoundException;
import com.cd.model.Todo;
import com.cd.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TodoService {

    @Autowired
    TodoRepository todoRepository;

    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodoById(UUID id) {
        return todoRepository.findById(id).orElseThrow(TodoNotFoundException::new);
    }

    public Todo addTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Todo todo) {
        getTodoById(todo.getId());
        return todoRepository.save(todo);
    }

    public boolean deleteTodo(UUID id) {
        getTodoById(id);
        todoRepository.deleteById(id);
        return true;
    }

    public List<Todo> getTodosByUser(String user) {

        return todoRepository.findAllByUser(user);
    }

    public List<Todo> getTodosBeforeTargetDate(LocalDate targetDate) {
        return todoRepository.findAllByTargetDateBefore(targetDate);
    }

}

