package com.backend.duolingo.controller;

import com.backend.duolingo.dto.CourseDTO;
import com.backend.duolingo.model.Course;
import com.backend.duolingo.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourseWithLessons(id));
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(course));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<CourseDTO>> getCoursesByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(courseService.getCoursesByLanguage(language));
    }
}