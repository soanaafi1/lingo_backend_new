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
    public ResponseEntity<LessonDTO> createLesson(@PathVariable UUID courseId, @RequestBody Lesson lesson) {
        Lesson createdLesson = lessonService.createLesson(courseId, lesson);
        LessonDTO lessonDTO = lessonService.convertToDTO(createdLesson);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonDTO);
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<LessonDTO> updateLesson(@PathVariable UUID id, @RequestBody Lesson lesson) {
        lesson.setId(id);
        Lesson updatedLesson = lessonService.updateLesson(lesson);
        LessonDTO lessonDTO = lessonService.convertToDTO(updatedLesson);
        return ResponseEntity.ok(lessonDTO);
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
        return getExercise(dto);
    }

    static Exercise getExercise(ExerciseDTO dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Exercise type cannot be null. Please specify a valid type (translation, multiple_choice, or matching).");
        }

        return switch (dto.getType()) {
            case "translation" -> {
                    TranslationExercise exercise = TranslationExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .exerciseOrder(dto.getOrder())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .correctAnswer(dto.getCorrectAnswer())
                        .build();

                    // Set a default value for correctOptionIndex to satisfy not-null constraint
                    exercise.setCorrectOptionIndex(0);

                    yield exercise;
                }
            case "multiple_choice" -> {
                    List<String> options = dto.getOptions();
                    int correctIndex = dto.getCorrectOptionIndex();
                    String correctAnswer = (options != null && correctIndex >= 0 && correctIndex < options.size())
                        ? options.get(correctIndex)
                        : "No correct answer";

                    yield MultipleChoiceExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .exerciseOrder(dto.getOrder())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .options(options)
                        .correctOptionIndex(correctIndex)
                        .correctAnswer(correctAnswer)
                        .build();
                }
            case "matching" -> {
                    Map<String, String> pairs = dto.getPairs();
                    // Create a string representation of the pairs map
                    String correctAnswer = pairs != null ?
                        pairs.entrySet().stream()
                            .map(entry -> entry.getKey() + ":" + entry.getValue())
                            .collect(java.util.stream.Collectors.joining(","))
                        : "No pairs";

                    yield MatchingExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .exerciseOrder(dto.getOrder())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .pairs(pairs)
                        .correctAnswer(correctAnswer)
                        .build();
                }
            default -> throw new IllegalArgumentException("Unknown exercise type: " + dto.getType());
        };
    }

    @GetMapping("/exercises/{id}")
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable UUID id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(ExerciseController.getExerciseDTO(exercise));
    }

    @DeleteMapping("/exercises/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable UUID id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    // User management endpoints
    @PostMapping("/users/make-admin")
    public ResponseEntity<String> createAdminUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setRole(Role.ADMIN);
        userRepository.save(user);

        return ResponseEntity.ok("User promoted to admin successfully");
    }
}
