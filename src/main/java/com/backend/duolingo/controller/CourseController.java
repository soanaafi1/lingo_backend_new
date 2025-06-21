package com.backend.duolingo.controller;

import com.backend.duolingo.dto.course.CourseDTO;
import com.backend.duolingo.dto.course.GetAllCoursesResponse;
import com.backend.duolingo.service.CourseService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<GetAllCoursesResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourseWithLessons(id));
    }
}