package com.backend.pandylingo.dto.lesson;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLessonRequest {
    @NotBlank
    private String title;

    @NotBlank
    @Size(min = 1, max = 100)
    private int xpReward;

    @NotBlank
    private Language language;

    @NotBlank
    private Difficulty difficulty;
}
