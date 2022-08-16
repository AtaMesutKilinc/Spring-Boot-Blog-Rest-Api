package com.works.repositories;

import com.works.entities.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByEmailEqualsIgnoreCase(String email);
}