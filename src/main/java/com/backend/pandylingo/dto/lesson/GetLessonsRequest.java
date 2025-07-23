package com.backend.pandylingo.dto.lesson;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetLessonsRequest {
    @NotBlank
    private Language language;

    @NotBlank
    private Difficulty difficulty;
}
