package com.backend.pandylingo.dto.course;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import lombok.Data;

@Data
public class CreateCourseRequest {
    private String courseName;
    private Language sourceLanguage;
    private Language targetLanguage;
    private String description;
    private Difficulty difficulty;
}
