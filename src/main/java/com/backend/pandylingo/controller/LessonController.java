package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.lesson.LessonDTO;
import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonsById(@PathVariable  UUID lessonId) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }

    @GetMapping
    public ResponseEntity<List<LessonDTO>> getLessonsWithLanguageAndDifficulty(Language language, Difficulty difficulty) {
        return ResponseEntity.ok(lessonService.getLessonsByLanguageAndDifficulty(language, difficulty));
    }
}
