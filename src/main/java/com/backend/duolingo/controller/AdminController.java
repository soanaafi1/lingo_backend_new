package com.backend.duolingo.controller;

import com.backend.duolingo.dto.CourseDTO;
import com.backend.duolingo.dto.ExerciseDTO;
import com.backend.duolingo.dto.LessonDTO;
import com.backend.duolingo.model.Course;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.Lesson;
import com.backend.duolingo.model.Role;
import com.backend.duolingo.model.User;
import com.backend.duolingo.model.TranslationExercise;
import com.backend.duolingo.model.MultipleChoiceExercise;
import com.backend.duolingo.model.MatchingExercise;
import com.backend.duolingo.controller.ExerciseController;
import com.backend.duolingo.repository.UserRepository;
import com.backend.duolingo.service.CourseService;
import com.backend.duolingo.service.ExerciseService;
import com.backend.duolingo.service.LessonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourseWithLessons(id));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(course));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable UUID id, @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(courseService.updateCourse(course));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // Lesson management endpoints
    @GetMapping("/lessons/course/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PostMapping("/lessons/course/{courseId}")
    public ResponseEntity<Lesson> createLesson(@PathVariable UUID courseId, @RequestBody Lesson lesson) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(courseId, lesson));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable UUID id, @RequestBody Lesson lesson) {
        lesson.setId(id);
        return ResponseEntity.ok(lessonService.updateLesson(lesson));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    // Exercise management endpoints
    @GetMapping("/exercises/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByLesson(@PathVariable UUID lessonId) {
        List<Exercise> exercises = exerciseService.getExercisesByLesson(lessonId);
        List<ExerciseDTO> exerciseDTOs = exercises.stream()
                .map(ExerciseController::getExerciseDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(exerciseDTOs);
    }

    @PostMapping("/exercises/lesson/{lessonId}")
    public ResponseEntity<ExerciseDTO> createExercise(@PathVariable UUID lessonId, @RequestBody ExerciseDTO exerciseDTO) {
        Exercise exercise = convertToExercise(exerciseDTO);
        Exercise savedExercise = exerciseService.createExercise(lessonId, exercise);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ExerciseController.getExerciseDTO(savedExercise));
    }

    @PutMapping("/exercises/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable UUID id, @RequestBody ExerciseDTO exerciseDTO) {
        Exercise exercise = convertToExercise(exerciseDTO);
        exercise.setId(id);
        Exercise updatedExercise = exerciseService.updateExercise(exercise);
        return ResponseEntity.ok(ExerciseController.getExerciseDTO(updatedExercise));
    }

    private Exercise convertToExercise(ExerciseDTO dto) {
        return switch (dto.getType()) {
            case "translation" -> TranslationExercise.builder()
                    .id(dto.getId())
                    .question(dto.getQuestion())
                    .hint(dto.getHint())
                    .exerciseOrder(dto.getOrder())
                    .xpReward(dto.getXpReward())
                    .heartsCost(dto.getHeartsCost())
                    .correctAnswer(dto.getCorrectAnswer())
                    .build();
            case "multiple_choice" -> MultipleChoiceExercise.builder()
                    .id(dto.getId())
                    .question(dto.getQuestion())
                    .hint(dto.getHint())
                    .exerciseOrder(dto.getOrder())
                    .xpReward(dto.getXpReward())
                    .heartsCost(dto.getHeartsCost())
                    .options(dto.getOptions())
                    .correctOptionIndex(dto.getCorrectOptionIndex())
                    .build();
            case "matching" -> MatchingExercise.builder()
                    .id(dto.getId())
                    .question(dto.getQuestion())
                    .hint(dto.getHint())
                    .exerciseOrder(dto.getOrder())
                    .xpReward(dto.getXpReward())
                    .heartsCost(dto.getHeartsCost())
                    .pairs(dto.getPairs())
                    .build();
            default -> throw new IllegalArgumentException("Unknown exercise type: " + dto.getType());
        };
    }

    @DeleteMapping("/exercises/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable UUID id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    // User management endpoints
    @PostMapping("/users/make-admin")
    public ResponseEntity<String> createAdminUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setRole(Role.ADMIN);
        userRepository.save(user);

        return ResponseEntity.ok("User promoted to admin successfully");
    }
}
