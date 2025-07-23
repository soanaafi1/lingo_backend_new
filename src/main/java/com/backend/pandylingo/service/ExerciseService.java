package com.backend.pandylingo.service;

import com.backend.pandylingo.exception.*;
import com.backend.pandylingo.model.Exercise;
import com.backend.pandylingo.model.Lesson;
import com.backend.pandylingo.model.MultipleChoiceExercise;
import com.backend.pandylingo.model.MatchingExercise;
import com.backend.pandylingo.repository.ExerciseRepository;
import com.backend.pandylingo.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;
    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    @Transactional
    public Exercise createExercise(UUID lessonId, Exercise exercise) {
        try {
            validateExercise(exercise);
            Lesson lesson = getLessonById(lessonId);
            exercise.setLesson(lesson); // Set the lesson relationship
            Exercise savedExercise = exerciseRepository.save(exercise);
            logger.info("Created exercise: {}", savedExercise.getId());
            return savedExercise;
        } catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation when creating exercise: {}", ex.getMessage());
            throw new BadRequestException("Exercise data is invalid or conflicts with existing data.");
        } catch (ObjectOptimisticLockingFailureException ex) {
            logger.error("Optimistic locking failure when creating exercise: {}", ex.getMessage());
            throw new ConflictException("Concurrent modification detected. Please try again.");
        } catch (DataAccessException ex) {
            logger.error("Database access error when creating exercise: {}", ex.getMessage());
            throw new InternalServerErrorException("Failed to create exercise due to a database error.");
        }
    }

    @Transactional
    public Exercise updateExercise(UUID exerciseId, Exercise updatedExercise) {
        try {
            validateExercise(updatedExercise);
            Exercise existingExercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new NotFoundException("Exercise not found with ID: " + exerciseId));

            // Update common fields
            existingExercise.setQuestion(updatedExercise.getQuestion());
            existingExercise.setHint(updatedExercise.getHint());
            existingExercise.setXpReward(updatedExercise.getXpReward());
            existingExercise.setHeartsCost(updatedExercise.getHeartsCost());
            existingExercise.setCorrectAnswer(updatedExercise.getCorrectAnswer()); // Update correct answer

            // Handle type-specific updates
            if (existingExercise instanceof MultipleChoiceExercise existingMCE && updatedExercise instanceof MultipleChoiceExercise updatedMCE) {
                existingMCE.setOptions(updatedMCE.getOptions());
            } else if (existingExercise instanceof MatchingExercise existingME && updatedExercise instanceof MatchingExercise updatedME) {
                existingME.setPairs(updatedME.getPairs());
            }
            // No specific updates needed for TranslationExercise beyond common fields

            Exercise savedExercise = exerciseRepository.save(existingExercise);
            logger.info("Updated exercise: {}", savedExercise.getId());
            return savedExercise;
        } catch (DataIntegrityViolationException ex) {
            logger.error("Data integrity violation when updating exercise: {}", ex.getMessage());
            throw new BadRequestException("Exercise data is invalid or conflicts with existing data.");
        } catch (ObjectOptimisticLockingFailureException ex) {
            logger.error("Optimistic locking failure when updating exercise: {}", ex.getMessage());
            throw new ConflictException("Concurrent modification detected. Please try again.");
        } catch (DataAccessException ex) {
            logger.error("Database access error when updating exercise: {}", ex.getMessage());
            throw new InternalServerErrorException("Failed to update exercise due to a database error.");
        }
    }

    public Exercise getExerciseById(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise not found with ID: " + exerciseId));
    }

    public List<Exercise> getExercisesByLessonId(UUID lessonId) {
        Lesson lesson = getLessonById(lessonId); // Ensure lesson exists
        if (lesson == null) {
            throw new NotFoundException("Lesson not found");
        }
        return exerciseRepository.findByLessonId(lessonId);
    }

    @Transactional
    public void deleteExercise(UUID exerciseId) {
        if (!exerciseRepository.existsById(exerciseId)) {
            throw new NotFoundException("Exercise not found with ID: " + exerciseId);
        }
        try {
            exerciseRepository.deleteById(exerciseId);
            logger.info("Deleted exercise: {}", exerciseId);
        } catch (DataAccessException ex) {
            logger.error("Database access error when deleting exercise: {}", ex.getMessage());
            throw new InternalServerErrorException("Failed to delete exercise due to a database error.");
        }
    }

    private Lesson getLessonById(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + lessonId));
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
        if (exercise.getCorrectAnswer() == null || exercise.getCorrectAnswer().isBlank()) {
            throw new BadRequestException("Correct answer is required for all exercise types");
        }

        if (exercise instanceof MultipleChoiceExercise mce) {
            if (mce.getOptions() == null || mce.getOptions().isEmpty()) {
                throw new BadRequestException("Options are required for multiple choice exercises");
            }
            // Ensure the provided correctAnswer for MC is one of the options
            if (!mce.getOptions().contains(mce.getCorrectAnswer())) {
                throw new BadRequestException("Correct answer for multiple choice must be one of the provided options.");
            }
        } else if (exercise instanceof MatchingExercise me) {
            if (me.getPairs() == null || me.getPairs().isEmpty()) {
                throw new BadRequestException("Pairs are required for matching exercises");
            }
            // For matching, ensure the correctAnswer string matches the serialized pairs
            String serializedPairs = me.getPairs().entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .sorted() // Sort to ensure consistent order for comparison
                    .collect(Collectors.joining(","));
            if (!serializedPairs.equalsIgnoreCase(me.getCorrectAnswer())) {
                throw new BadRequestException("Correct answer for matching exercise does not match the provided pairs.");
            }
        }
    }
}
