package com.works.repositories;

import com.works.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Override
    Optional<Author> findById(Long id);

    Optional<Author> findByEmailEqualsIgnoreCase(String email);
}