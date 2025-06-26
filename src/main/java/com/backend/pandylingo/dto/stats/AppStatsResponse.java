package com.backend.pandylingo.dto.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppStatsResponse {
    private long totalCourses;
    private long totalLessons;
    private long totalExercises;
    private long totalLearners;
}
