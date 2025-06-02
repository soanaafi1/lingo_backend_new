package com.backend.duolingo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonProgress {
    private int completedExercises;
    private int totalExercises;
    private double completionPercentage;

    public LessonProgress(int completedExercises, int totalExercises, double completionPercentage) {
        this.completedExercises = completedExercises;
        this.totalExercises = totalExercises;
        this.completionPercentage = completionPercentage;
    }

    public LessonProgress(int completedExercises, int totalExercises) {
        this(completedExercises, totalExercises,
                totalExercises > 0 ? (completedExercises * 100.0) / totalExercises : 0);
    }
}
