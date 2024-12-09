package com.cd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Todo {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="todouser")
    @NotBlank (message = "User cannot be null or empty!")
    private String user;

    @Size(min = 4, max = 10, message = "Title should be 4 to 10")
    private String title;

    @Size(min = 10, max = 200, message = "Task should be b/w 10-200 characters!")
    private String task;
    private boolean done;

    @FutureOrPresent(message = "Target date must be present or future date!")
    private LocalDate targetDate;
}
