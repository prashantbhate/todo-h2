package com.cd.controller.advices;

import com.cd.common.exceptions.TodoIDMismatchException;
import com.cd.common.exceptions.TodoNotFoundException;
import com.cd.common.utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class TodoControllerAdvice {

    private static void logException(Exception ex) {
        ex.printStackTrace();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseEntity handleException(TodoNotFoundException ex){
        logException(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("Todo-404");
        errorResponse.setErrorMessage("Todo Not found!");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseEntity handleException(TodoIDMismatchException ex){
        logException(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("Todo-400");
        errorResponse.setErrorMessage("Todo ID in the path is not same as the ID of Todo!");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseEntity handleException(MethodArgumentNotValidException ex){
        logException(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("Todo-400");
        errorResponse.setErrorMessage("Todo is Not Valid!");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setFieldErrors(ex.getFieldErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler
    public ResponseEntity handleException(Exception ex){
        logException(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("Todo-500");
        errorResponse.setErrorMessage("An Server Exception occurred!");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
