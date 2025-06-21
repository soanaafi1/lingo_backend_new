package com.backend.duolingo.repository;

import com.backend.duolingo.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByCourseId(UUID courseId);

    boolean existsByTitleAndCourseId(String title, UUID id);
}
