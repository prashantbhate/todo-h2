package com.cd.repository;

import com.cd.model.Tudor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TudorRepository extends JpaRepository<Tudor, Long> {
    Optional<Tudor> findByUsername(String username);
}
