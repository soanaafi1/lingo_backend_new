package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.lesson.LessonDTO;
import com.backend.pandylingo.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }
}
