package com.backend.duolingo.controller;

import com.backend.duolingo.dto.LessonDTO;
import com.backend.duolingo.model.Lesson;
import com.backend.duolingo.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<Lesson> createLesson(
            @PathVariable UUID courseId,
            @RequestBody Lesson lesson) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(courseId, lesson));
    }
}
