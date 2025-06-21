package com.backend.duolingo.dto;

import com.backend.duolingo.model.Difficulty;
import com.backend.duolingo.model.Language;
import lombok.Data;

@Data
public class CreateCourseRequest {
    private String courseName;
    private Language sourceLanguage;
    private Language targetLanguage;
    private String description;
    private Difficulty difficulty;


}
