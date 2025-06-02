package com.backend.duolingo.dto;

import com.backend.duolingo.model.Difficulty;
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
    private String language;
    private String iconUrl;
    private String description;
    private Difficulty difficulty;
    private List<LessonDTO> lessons;
}
