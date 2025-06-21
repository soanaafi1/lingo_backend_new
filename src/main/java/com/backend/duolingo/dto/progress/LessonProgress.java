package com.backend.duolingo.dto.progress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgress {
    private UUID lessonId;
    private int completedExercises;
    private int totalExercises;
    private int correctExercises;
    private int percentComplete;
    private int percentCorrect;

    // For backward compatibility
    public LessonProgress(int completedExercises, int totalExercises, double completionPercentage) {
        this.completedExercises = completedExercises;
        this.totalExercises = totalExercises;
        this.percentComplete = (int)completionPercentage;
    }

    public LessonProgress(int completedExercises, int totalExercises) {
        this.completedExercises = completedExercises;
        this.totalExercises = totalExercises;
        this.percentComplete = totalExercises > 0 ? (completedExercises * 100) / totalExercises : 0;
    }
}
