package com.backend.pandylingo.dto.exercise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private UUID id;
    private String type;
    private String question;
    private String hint;
    private int order;
    private int xpReward;
    private int heartsCost;

    // Type-specific fields
    private String correctAnswer;          // For translation
    private List<String> options;          // For multiple choice
    private Integer correctOptionIndex;    // For multiple choice
    private Map<String, String> pairs;     // For matching
}
