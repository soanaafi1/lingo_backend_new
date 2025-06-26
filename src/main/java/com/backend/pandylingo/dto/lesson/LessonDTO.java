package com.backend.pandylingo.dto.lesson;

import com.backend.pandylingo.dto.exercise.ExerciseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private UUID id;
    private String title;
    private int xpReward;
    private List<ExerciseDTO> exercises;
}
