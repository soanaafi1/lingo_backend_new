package com.backend.duolingo.controller;

import com.backend.duolingo.dto.CourseDTO;
import com.backend.duolingo.dto.CreateCourseRequest;
import com.backend.duolingo.dto.GetAllCoursesResponse;
import com.backend.duolingo.model.Course;
import com.backend.duolingo.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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