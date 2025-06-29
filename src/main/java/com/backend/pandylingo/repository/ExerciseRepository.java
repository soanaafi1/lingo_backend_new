package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByLessonId(UUID lessonId);

    boolean existsByQuestionAndLessonId(String question, UUID id);

}
