package com.backend.pandylingo.service;

import com.backend.pandylingo.dto.progress.LessonProgress;
import com.backend.pandylingo.model.Exercise;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.model.UserProfile;
import com.backend.pandylingo.model.UserProgress;
import com.backend.pandylingo.repository.ExerciseRepository;
import com.backend.pandylingo.repository.UserProfileRepository;
import com.backend.pandylingo.repository.UserProgressRepository;
import com.backend.pandylingo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final UserProgressRepository progressRepository;
    private final UserProfileRepository userProfileRepository;
    private final ExerciseRepository exerciseRepository;
    private final LessonService lessonService;

    @Transactional
    public UserProgress submitExercise(UUID userId, UUID exerciseId, String answer) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        // Check if user has already completed this exercise
        Optional<UserProgress> existingProgress =
                progressRepository.findByUserIdAndExerciseId(userId, exerciseId);

        if (existingProgress.isPresent() && existingProgress.get().isCompleted()) {
            throw new IllegalStateException("Exercise already completed");
        }

        // Validate answer
        boolean isCorrect = exercise.validateAnswer(answer);
        int heartsUsed = exercise.getHeartsCost();

        // Check if user has enough hearts
        if (userProfile.getHearts() < heartsUsed) {
            throw new NotEnoughHeartsException("Not enough hearts to attempt this exercise");
        }

        // Update user stats
        userProfile.setXpPoints(userProfile.getXpPoints() + exercise.getXpReward());
        userProfile.setHearts(userProfile.getHearts() - heartsUsed);

        // Update streak
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate lastStreakUpdate = userProfile.getLastStreakUpdate().toLocalDate();

        // If this is the first exercise completed today, update streak
        if (!lastStreakUpdate.equals(today)) {
            // If last practice was yesterday, increment streak
            if (lastStreakUpdate.equals(today.minusDays(1))) {
                userProfile.setStreak(userProfile.getStreak() + 1);
            } 
            // If last practice was more than a day ago but user has streak freeze
            else if (userProfile.getStreakFreezeCount() > 0) {
                userProfile.setStreakFreezeCount(userProfile.getStreakFreezeCount() - 1);
            } 
            // If last practice was more than a day ago and no streak freeze, reset streak to 1
            else {
                userProfile.setStreak(1);
            }

            userProfile.setLastStreakUpdate(now);
        }

        userProfileRepository.save(userProfile);

        // Create or update progress record
        UserProgress progress = existingProgress.orElseGet(() ->
                UserProgress.builder()
                        .user(userProfile)
                        .exercise(exercise)
                        .build()
        );

        progress.setCompleted(true);
        progress.setCorrect(isCorrect);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setUserAnswer(answer);
        progress.setXpEarned(exercise.getXpReward());
        progress.setHeartsUsed(heartsUsed);

        return progressRepository.save(progress);
    }

    public List<UserProgress> getUserProgress(UUID userId) {
        return progressRepository.findByUserId(userId);
    }

    public LessonProgress getLessonProgress(UUID userId, UUID lessonId) {
        int completed = progressRepository.countCompletedExercisesByUserAndLesson(userId, lessonId);
        int correct = progressRepository.countCorrectExercisesByUserAndLesson(userId, lessonId);
        int totalExercises = lessonService.countExercisesInLesson(lessonId);

        int percentComplete = totalExercises > 0 ? (completed * 100) / totalExercises : 0;
        int percentCorrect = completed > 0 ? (correct * 100) / completed : 0;

        return LessonProgress.builder()
                .lessonId(lessonId)
                .completedExercises(completed)
                .correctExercises(correct)
                .totalExercises(totalExercises)
                .percentComplete(percentComplete)
                .percentCorrect(percentCorrect)
                .build();
    }

    // Custom exception
    public static class NotEnoughHeartsException extends RuntimeException {
        public NotEnoughHeartsException(String message) {
            super(message);
        }
    }
}
