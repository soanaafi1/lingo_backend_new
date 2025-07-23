package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {

    // Find all progress records for a specific user
    List<UserProgress> findByUserId(UUID userId);

    // Find progress for a specific user and exercise
    Optional<UserProgress> findByUserIdAndExerciseId(UUID userId, UUID exerciseId);


    // Count how many exercises a user has completed in a lesson
    @Query("SELECT COUNT(up) FROM UserProgress up " +
            "WHERE up.user.id = :userId " +
            "AND up.exercise.lesson.id = :lessonId " +
            "AND up.completed = true")
    int countCompletedExercisesByUserAndLesson(
            @Param("userId") UUID userId,
            @Param("lessonId") UUID lessonId);

    // Count how many exercises a user has correctly completed in a lesson
    @Query("SELECT COUNT(up) FROM UserProgress up " +
            "WHERE up.user.id = :userId " +
            "AND up.exercise.lesson.id = :lessonId " +
            "AND up.completed = true " +
            "AND up.correct = true")
    int countCorrectExercisesByUserAndLesson(
            @Param("userId") UUID userId,
            @Param("lessonId") UUID lessonId);
}
