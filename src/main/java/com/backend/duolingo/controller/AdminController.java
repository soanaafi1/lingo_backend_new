package com.backend.duolingo.controller;

import com.backend.duolingo.dto.*;
import com.backend.duolingo.exception.*;
import com.backend.duolingo.model.*;
import com.backend.duolingo.repository.UserRepository;
import com.backend.duolingo.service.AppStatsService;
import com.backend.duolingo.service.CourseService;
import com.backend.duolingo.service.ExerciseService;
import com.backend.duolingo.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final CourseService courseService;
    private final LessonService lessonService;
    private final ExerciseService exerciseService;
    private final UserRepository userRepository;

    // Course management endpoints
    @GetMapping("/courses")
    public ResponseEntity<List<GetAllCoursesResponse>> getAllCourses() {
        try {
            return ResponseEntity.ok(courseService.getAllCourses());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve courses", ex.getMessage());
        }
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(courseService.getCourseWithLessons(id));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve course", ex.getMessage());
        }
    }

    @PostMapping("/courses")
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(courseService.createCourse(request));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create course", ex.getMessage());
        }
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable UUID id, @RequestBody Course course) {
        try {
            course.setId(id);
            return ResponseEntity.ok(courseService.updateCourse(course));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update course", ex.getMessage());
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to delete course", ex.getMessage());
        }
    }

    // Lesson management endpoints
    @GetMapping("/lessons/course/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable UUID courseId) {
        try {
            return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve lessons", ex.getMessage());
        }
    }

    @PostMapping("/lessons/course/{courseId}")
    public ResponseEntity<LessonDTO> createLesson(@PathVariable UUID courseId, @RequestBody Lesson lesson) {
        try {
            Lesson createdLesson = lessonService.createLesson(courseId, lesson);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(lessonService.convertToDTO(createdLesson));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create lesson", ex.getMessage());
        }
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<LessonDTO> updateLesson(@PathVariable UUID id, @RequestBody Lesson lesson) {
        try {
            lesson.setId(id);
            Lesson updatedLesson = lessonService.updateLesson(lesson);
            return ResponseEntity.ok(lessonService.convertToDTO(updatedLesson));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update lesson", ex.getMessage());
        }
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        try {
            lessonService.deleteLesson(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to delete lesson", ex.getMessage());
        }
    }

    // Exercise management endpoints
    @GetMapping("/exercises/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByLesson(@PathVariable UUID lessonId) {
        try {
            List<Exercise> exercises = exerciseService.getExercisesByLesson(lessonId);
            return ResponseEntity.ok(exercises.stream()
                    .map(ExerciseController::getExerciseDTO)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve exercises", ex.getMessage());
        }
    }

    @PostMapping("/exercises/lesson/{lessonId}")
    public ResponseEntity<ExerciseDTO> createExercise(@PathVariable UUID lessonId, @RequestBody ExerciseDTO exerciseDTO) {
        try {
            Exercise exercise = convertToExercise(exerciseDTO);
            Exercise savedExercise = exerciseService.createExercise(lessonId, exercise);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ExerciseController.getExerciseDTO(savedExercise));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create exercise", ex.getMessage());
        }
    }

    @PutMapping("/exercises/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable UUID id, @RequestBody ExerciseDTO exerciseDTO) {
        try {
            Exercise exercise = convertToExercise(exerciseDTO);
            exercise.setId(id);
            Exercise updatedExercise = exerciseService.updateExercise(exercise);
            return ResponseEntity.ok(ExerciseController.getExerciseDTO(updatedExercise));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update exercise", ex.getMessage());
        }
    }

    @DeleteMapping("/exercises/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable UUID id) {
        try {
            exerciseService.deleteExercise(id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to delete exercise", ex.getMessage());
        }
    }

    // User management endpoints
    @PostMapping("/users/make-admin")
    public ResponseEntity<String> createAdminUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isBlank()) {
                throw new BadRequestException("Email is required");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

            if (user.getRole() == Role.ADMIN) {
                throw new ConflictException("User is already an admin");
            }

            user.setRole(Role.ADMIN);
            userRepository.save(user);

            return ResponseEntity.ok("User promoted to admin successfully");
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update user role", ex.getMessage());
        }
    }

    private Exercise convertToExercise(ExerciseDTO dto) {
        try {
            return ExerciseController.getExercise(dto);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}