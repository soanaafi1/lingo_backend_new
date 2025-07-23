package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findAllByLanguageAndDifficulty(Language language, Difficulty difficulty);

    boolean existsByTitle(String title);
}
