package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.course.CourseDTO;
import com.backend.pandylingo.dto.course.GetAllCoursesResponse;
import com.backend.pandylingo.service.CourseService;
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