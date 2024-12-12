package com.cd.controller;

import com.cd.model.Todo;
import com.cd.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional // Ensures database is rolled back after each test
class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private TodoRepository todoRepository;

    // Clear the repository before each test
    @BeforeEach
    void clearRepository() {
        todoRepository.deleteAll();
    }

    @Test
    void testAddTodo_Success() throws Exception {
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "user": "User1",
                                    "title": "TestTask",
                                    "task": "This is a test task description.",
                                    "done": false,
                                    "targetDate": "2024-12-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user").value("User1"))
                .andExpect(jsonPath("$.title").value("TestTask"))
                .andExpect(jsonPath("$.task").value("This is a test task description."))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void testAddTodo_ValidationError() throws Exception {
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "user": "",
                                    "title": "T",
                                    "task": "Short",
                                    "done": false,
                                    "targetDate": "2020-01-01"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Todo-400"))
                .andExpect(jsonPath("$.errorMessage").value("Todo is Not Valid!"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(4)))  // Expecting 4 validation errors

                // Validate individual field errors
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title')].defaultMessage")
                        .value("Title should be 4 to 10"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='user')].defaultMessage")
                        .value("User cannot be null or empty!"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='task')].defaultMessage")
                        .value("Task should be b/w 10-200 characters!"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='targetDate')].defaultMessage")
                        .value("Target date must be present or future date!"));
    }

    @Test
    void testUpdateTodo_Success() throws Exception {
        // Insert a test Todo
        Todo savedTodo = todoRepository.save(new Todo(
                null,
                "User1",
                "OldTitle",
                "This is the old task description.",
                false,
                LocalDate.of(2024, 12, 31)
        ));

        // Create a modified Todo object for update
        Todo updatedTodo = new Todo(
                savedTodo.getId(),
                "User1",
                "Title", // Updated title
                "This is the updated task description.", // Updated task
                true, // Marked as done
                LocalDate.of(2025, 1, 1) // Updated target date
        );

        // Perform the PUT request to update the Todo
        mockMvc.perform(put("/todos/{id}", savedTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "%s",
                                    "user": "User1",
                                    "title": "Title1",
                                    "task": "This is the updated task description.",
                                    "done": true,
                                    "targetDate": "2025-01-01"
                                }
                                """.formatted(savedTodo.getId().toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTodo.getId().toString()))
                .andExpect(jsonPath("$.user").value("User1"))
                .andExpect(jsonPath("$.title").value("Title1"))
                .andExpect(jsonPath("$.task").value("This is the updated task description."))
                .andExpect(jsonPath("$.done").value(true))
                .andExpect(jsonPath("$.targetDate").value("2025-01-01"));
    }

    @Test
    void testUpdateTodo_IDMismatch() throws Exception {
        Todo savedTodo = todoRepository.save(new Todo(
                null,
                "User1",
                "OldTitle",
                "This is the old task description.",
                false,
                LocalDate.of(2024, 12, 31)
        ));

        mockMvc.perform(put("/todos/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "%s",
                                    "user": "User1",
                                    "title": "Title1",
                                    "task": "This is the updated task description.",
                                    "done": true,
                                    "targetDate": "2025-01-01"
                                }
                                """.formatted(savedTodo.getId().toString()))) // Passing correct Todo
                .andDo(print())
                .andExpect(status().isBadRequest())  // Expect 400 due to ID mismatch
                .andExpect(jsonPath("$.errorCode").value("Todo-400"))
                .andExpect(jsonPath("$.errorMessage").value("Todo ID in the path is not same as the ID of Todo!"));
    }

    @Test
    void testUpdateTodo_ValidationError() throws Exception {
        // Insert a test Todo
        Todo savedTodo = todoRepository.save(new Todo(
                null,
                "User1",
                "OldTitle",
                "This is the old task description.",
                false,
                LocalDate.of(2024, 12, 31)
        ));

        // Perform the PUT request with invalid data
        mockMvc.perform(put("/todos/{id}", savedTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "%s",
                                    "user": "User1",
                                    "title": "T",
                                    "task": "Short",
                                    "done": false,
                                    "targetDate": "2020-01-01"
                                }
                                """.formatted(savedTodo.getId().toString())))
                .andExpect(status().isBadRequest())
                // Validate individual field errors
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='title')].defaultMessage")
                        .value("Title should be 4 to 10"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='task')].defaultMessage")
                        .value("Task should be b/w 10-200 characters!"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='targetDate')].defaultMessage")
                        .value("Target date must be present or future date!"));
    }

    @Test
    void testGetTodos_Success() throws Exception {
        // Insert test data
        todoRepository.save(new Todo(
                null,
                "User1",
                "Task1",
                "This is a test task 1.",
                false,
                LocalDate.of(2024, 12, 31)
        ));
        todoRepository.save(new Todo(
                null,
                "User2",
                "Task2",
                "This is a test task 2.",
                true,
                LocalDate.of(2025, 1, 1)
        ));

        mockMvc.perform(get("/todos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)) // Expect 2 todos
                .andExpect(jsonPath("$[0].user").value("User1"))
                .andExpect(jsonPath("$[1].user").value("User2"));
    }

    @Test
    void testGetTodoById_Success() throws Exception {
        // Create and save a test Todo
        String testUser = "TestUser";
        String testTitle = "TestTitle";
        String expectedValue = "This is a test task description.";
        Todo savedTodo = todoRepository.save(new Todo(
                null,
                testUser,
                testTitle,
                expectedValue,
                false,
                LocalDate.of(2024, 12, 31)
        ));
        mockMvc.perform(get("/todos/{id}", savedTodo.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTodo.getId().toString()))
                .andExpect(jsonPath("$.user").value(testUser))
                .andExpect(jsonPath("$.title").value(testTitle))
                .andExpect(jsonPath("$.task").value(expectedValue))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void testGetTodoById_NotFound() throws Exception {
        // Use a non-existent UUID for the test
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/todos/{id}", nonExistentId))
                .andDo(print())  // Print the response to console
                .andExpect(status().isNotFound())  // Expect 404 Not Found
                .andExpect(jsonPath("$.errorCode").value("Todo-404"))
                .andExpect(jsonPath("$.errorMessage").value("Todo Not found!"));
    }

    @Test
    void testDeleteTodo_Success() throws Exception {
        // Insert a test Todo
        Todo savedTodo = todoRepository.save(new Todo(
                null,
                "UserToDelete",
                "Task",
                "This task will be deleted.",
                false,
                LocalDate.of(2024, 12, 31)
        ));

        // Perform DELETE request
        mockMvc.perform(delete("/todos/{id}", savedTodo.getId()))
                .andExpect(status().isNoContent());

        // Verify the Todo no longer exists in the database
        boolean exists = todoRepository.existsById(savedTodo.getId());
        assertFalse(exists, "Todo should be deleted from the database");
    }

    @Test
    void testDeleteTodo_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/todos/{id}", nonExistentId))
                .andDo(print())  // Print the response to console
                .andExpect(status().isNotFound())  // Expect 404 Not Found
                .andExpect(jsonPath("$.errorCode").value("Todo-404"))
                .andExpect(jsonPath("$.errorMessage").value("Todo Not found!"));
    }
}
