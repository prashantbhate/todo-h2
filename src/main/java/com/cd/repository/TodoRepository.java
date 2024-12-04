package com.cd.repository;

import com.cd.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo,UUID> {
    List<Todo> findAllByUser(String user);

    List<Todo> findAllByTargetDateBefore(LocalDate targetDate);

}
