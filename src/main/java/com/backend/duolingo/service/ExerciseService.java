package com.backend.duolingo.service;

import com.backend.duolingo.exception.*;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.Lesson;
import com.backend.duolingo.repository.ExerciseRepository;
import com.backend.duolingo.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;
    private final AppStatsService appStatsService;

    @Transactional
    public Exercise createExercise(UUID lessonId, Exercise exercise) {
        try {
            validateExercise(exercise);
            Lesson lesson = getLessonById(lessonId);

            if (exerciseRepository.existsByQuestionAndLessonId(exercise.getQuestion(), lessonId)) {
                throw new ConflictException("Exercise with this question already exists in the lesson");
            }

            exercise.setLesson(lesson);
            Exercise savedExercise = exerciseRepository.save(exercise);
            appStatsService.incrementExercisesCount();
            return savedExercise;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid exercise data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create exercise", ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Exercise> getExercisesByLesson(UUID lessonId) {
        try {
            if (!lessonRepository.existsById(lessonId)) {
                throw new NotFoundException(Lesson.class, lessonId);
            }
            return exerciseRepository.findByLessonId(lessonId);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve exercises", ex.getMessage());
        }
    }

    @Transactional
    public Exercise updateExercise(Exercise exercise) {
        try {
            // Verify exercise exists and get current version
            Exercise existing = exerciseRepository.findById(exercise.getId())
                    .orElseThrow(() -> new NotFoundException(Exercise.class, exercise.getId()));

            validateExercise(exercise);

            // Check for question conflicts in the same lesson
            if (!existing.getQuestion().equals(exercise.getQuestion())) {
                if (exerciseRepository.existsByQuestionAndLessonId(
                        exercise.getQuestion(),
                        existing.getLesson().getId()
                )) {
                    throw new ConflictException("Exercise with this question already exists in the lesson");
                }
            }

            // Preserve the original lesson if not changed
            if (exercise.getLesson() == null) {
                exercise.setLesson(existing.getLesson());
            } else if (!exercise.getLesson().getId().equals(existing.getLesson().getId())) {
                // Verify new lesson exists
                getLessonById(exercise.getLesson().getId());
            }

            return exerciseRepository.save(exercise);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConflictException("Exercise was modified by another user. Please refresh and try again.");
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid exercise data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update exercise", ex.getMessage());
        }
    }

    @Transactional
    public void deleteExercise(UUID exerciseId) {
        try {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new NotFoundException(Exercise.class, exerciseId));

            exerciseRepository.delete(exercise);
            appStatsService.decrementExercises();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to delete exercise", ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Exercise getExerciseById(UUID exerciseId) {
        try {
            return exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new NotFoundException(Exercise.class, exerciseId));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve exercise", ex.getMessage());
        }
    }

    // Helper methods
    private Lesson getLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(Lesson.class, lessonId));
    }

    private void validateExercise(Exercise exercise) {
        if (exercise.getQuestion() == null || exercise.getQuestion().isBlank()) {
            throw new BadRequestException("Exercise question is required");
        }
        if (exercise.getXpReward() < 0) {
            throw new BadRequestException("XP reward cannot be negative");
        }
        if (exercise.getHeartsCost() < 0) {
            throw new BadRequestException("Hearts cost cannot be negative");
        }
    }
}