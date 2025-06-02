package com.backend.duolingo.service;

import com.backend.duolingo.dto.LessonProgress;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.User;
import com.backend.duolingo.model.UserProgress;
import com.backend.duolingo.repository.ExerciseRepository;
import com.backend.duolingo.repository.UserProgressRepository;
import com.backend.duolingo.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public UserProgress submitExercise(UUID userId, UUID exerciseId, String answer) {
        User user = userRepository.findById(userId)
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
        if (user.getHearts() < heartsUsed) {
            throw new NotEnoughHeartsException("Not enough hearts to attempt this exercise");
        }

        // Update user stats
        user.setXpPoints(user.getXpPoints() + exercise.getXpReward());
        user.setHearts(user.getHearts() - heartsUsed);

        // Update streak
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate lastStreakUpdate = user.getLastStreakUpdate().toLocalDate();

        // If this is the first exercise completed today, update streak
        if (!lastStreakUpdate.equals(today)) {
            // If last practice was yesterday, increment streak
            if (lastStreakUpdate.equals(today.minusDays(1))) {
                user.setStreak(user.getStreak() + 1);
            } 
            // If last practice was more than a day ago but user has streak freeze
            else if (user.getStreakFreezeCount() > 0) {
                user.setStreakFreezeCount(user.getStreakFreezeCount() - 1);
            } 
            // If last practice was more than a day ago and no streak freeze, reset streak to 1
            else {
                user.setStreak(1);
            }

            user.setLastStreakUpdate(now);
        }

        userRepository.save(user);

        // Create or update progress record
        UserProgress progress = existingProgress.orElseGet(() ->
                UserProgress.builder()
                        .user(user)
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
        // You'll need to get total exercises from LessonService
        int totalExercises = 10; // Placeholder

        return new LessonProgress(completed, totalExercises);
    }

    // Custom exception
    public static class NotEnoughHeartsException extends RuntimeException {
        public NotEnoughHeartsException(String message) {
            super(message);
        }
    }
}
