package com.backend.pandylingo.dto.course;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetAllCoursesResponse {
    private UUID id;
    private String courseName;
    private Language sourceLanguage;
    private Language targetLanguage;
    private String description;
    private Difficulty difficulty;
}
