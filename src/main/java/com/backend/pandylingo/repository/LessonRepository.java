package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByCourseId(UUID courseId);

    boolean existsByTitleAndCourseId(String title, UUID id);
}
