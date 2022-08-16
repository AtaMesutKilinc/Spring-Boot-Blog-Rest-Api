package com.works.repositories;

import com.works.entities.Writing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WritingRepository extends JpaRepository<Writing, Long> {
    List<Writing> findByAuthor_IdEquals(Long id);

    List<Writing> findByTitleContainsIgnoreCaseOrTextContainsIgnoreCase(String title, String text);



}