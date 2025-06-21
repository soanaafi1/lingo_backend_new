package com.backend.duolingo.dto.course;

import com.backend.duolingo.dto.lesson.LessonDTO;
import com.backend.duolingo.model.Difficulty;
import com.backend.duolingo.model.Language;
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
public class CourseDTO {
    private UUID id;
    private String name;
    private Language sourceLanguage;
    private Language targetLanguage;
    private String description;
    private Difficulty difficulty;
    private List<LessonDTO> lessons;
}
