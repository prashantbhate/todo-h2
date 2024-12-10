package com.cd.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.cd.common.exceptions.TodoIDMismatchException;
import com.cd.model.Todo;
import com.cd.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    private Todo testTodo;
    private UUID testId;

    @BeforeEach
    public void setup() {
        testId = UUID.randomUUID();
        testTodo = new Todo();
        testTodo.setId(testId);
        testTodo.setTitle("Test Todo");
        testTodo.setTask("Test TodoTest TodoTest TodoTest TodoTest TodoTest TodoTest TodoTest Todo");
        testTodo.setUser("testUser");
        testTodo.setTargetDate(LocalDate.now());
    }

    @Test
    public void testGetTodos() {
        List<Todo> mockTodos = Collections.singletonList(testTodo);
        when(todoService.getTodos()).thenReturn(mockTodos);

        ResponseEntity<List<Todo>> response = todoController.getTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(testTodo, response.getBody().get(0));
        verify(todoService).getTodos();
    }

    @Test
    public void testGetTodoById() {
        when(todoService.getTodoById(testId)).thenReturn(testTodo);

//        ResponseEntity<Todo> response =
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(testTodo, response.getBody());
//        verify(todoService).getTodoById(testId);
    }

    @Test
    public void testAddTodos() throws URISyntaxException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));


        when(todoService.addTodo(any(Todo.class))).thenReturn(testTodo);
        ResponseEntity<Todo> response = todoController.addTodos(testTodo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTodo, response.getBody());
        verify(todoService).addTodo(testTodo);
    }

    @Test
    public void testGetTodosByUser() {
        List<Todo> mockTodos = Collections.singletonList(testTodo);
        when(todoService.getTodosByUser("testUser")).thenReturn(mockTodos);

//        ResponseEntity<List<Todo>> response =
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
//        assertEquals(testTodo, response.getBody().get(0));
//        verify(todoService).getTodosByUser("testUser");
    }

    @Test
    public void testUpdateTodo() {
        when(todoService.updateTodo(testTodo)).thenReturn(testTodo);

//        Todo updatedTodo =
//
//        assertEquals(testTodo, updatedTodo);
//        verify(todoService).updateTodo(testTodo);
    }

    @Test
    public void testUpdateTodoWithIdMismatch() {
        Todo differentTodo = new Todo();
        differentTodo.setId(UUID.randomUUID());

        assertThrows(TodoIDMismatchException.class, () -> todoController.updateTodo(testId.toString(), differentTodo));
    }

    @Test
    public void testDeleteTodoSuccess() {
        when(todoService.deleteTodo(testId)).thenReturn(true);

//        ResponseEntity<?> response =
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(todoService).deleteTodo(testId);
    }

    @Test
    public void testDeleteTodoNotFound() {
        when(todoService.deleteTodo(testId)).thenReturn(false);

//        ResponseEntity<?> response =
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(todoService).deleteTodo(testId);
    }

    @Test
    public void testGetTodosByTargetDate() {
        LocalDate testDate = LocalDate.now();
        List<Todo> mockTodos = Collections.singletonList(testTodo);
        when(todoService.getTodosBeforeTargetDate(testDate)).thenReturn(mockTodos);

        ResponseEntity<List<Todo>> response = todoController.getTodosByUser(testDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(testTodo, response.getBody().get(0));
        verify(todoService).getTodosBeforeTargetDate(testDate);
    }
}