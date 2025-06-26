package com.backend.pandylingo.dto.progress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseProgress {
    private int completedLessons;
    private int totalLessons;
    private int completedExercises;
    private int totalExercises;
    private double completionPercentage;
    private int xpEarned;
}
