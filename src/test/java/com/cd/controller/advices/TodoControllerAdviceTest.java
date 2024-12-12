package com.cd.controller.advices;


import com.cd.common.exceptions.TodoIDMismatchException;
import com.cd.common.exceptions.TodoNotFoundException;
import com.cd.common.utils.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoControllerAdviceTest {

    private TodoControllerAdvice controllerAdvice;

    @BeforeEach
    public void setup() {
        controllerAdvice = new TodoControllerAdvice();
    }

    @Test
    public void testHandleTodoNotFoundException() {
        TodoNotFoundException exception = new TodoNotFoundException();

        ResponseEntity<?> responseEntity = controllerAdvice.handleException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals("Todo-404", errorResponse.getErrorCode());
        assertEquals("Todo Not found!", errorResponse.getErrorMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void testHandleTodoIDMismatchException() {
        TodoIDMismatchException exception = new TodoIDMismatchException();

        ResponseEntity<?> responseEntity = controllerAdvice.handleException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals("Todo-400", errorResponse.getErrorCode());
        assertEquals("Todo ID in the path is not same as the ID of Todo!", errorResponse.getErrorMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        // Create a mock MethodArgumentNotValidException
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        // Create mock field errors
        FieldError fieldError1 = new FieldError("objectName", "field1", "error message 1");
        FieldError fieldError2 = new FieldError("objectName", "field2", "error message 2");
        List<FieldError> fieldErrors = List.of(fieldError1, fieldError2);

        // Setup mock behavior
        when(exception.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<?> responseEntity = controllerAdvice.handleException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals("Todo-400", errorResponse.getErrorCode());
        assertEquals("Todo is Not Valid!", errorResponse.getErrorMessage());
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(fieldErrors, errorResponse.getFieldErrors());
    }

    @Test
    public void testHandleGenericException() {
        Exception exception = new Exception("Generic error");

        ResponseEntity<?> responseEntity = controllerAdvice.handleException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals("Todo-500", errorResponse.getErrorCode());
        assertEquals("An Server Exception occurred!", errorResponse.getErrorMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    public void testTimestampGeneration() {
        TodoNotFoundException exception = new TodoNotFoundException();

        ResponseEntity<?> responseEntity = controllerAdvice.handleException(exception);
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();

        assert errorResponse != null;
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(errorResponse.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
}