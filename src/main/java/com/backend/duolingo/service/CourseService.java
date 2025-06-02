package com.backend.duolingo.service;

import com.backend.duolingo.dto.CourseDTO;
import com.backend.duolingo.dto.ExerciseDTO;
import com.backend.duolingo.dto.LessonDTO;
import com.backend.duolingo.model.*;
import com.backend.duolingo.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAllWithLessons().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseWithLessons(UUID courseId) {
        Course course = courseRepository.findByIdWithLessons(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        return convertToDTO(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByLanguage(String language) {
        return courseRepository.findByLanguage(language).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(Course course) {
        Course savedCourse = courseRepository.save(course);
        // Refresh the course to get the lessons
        courseRepository.flush();
        Course refreshedCourse = courseRepository.findById(savedCourse.getId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        return convertToDTO(refreshedCourse);
    }

    @Transactional
    public CourseDTO updateCourse(Course course) {
        // Check if course exists
        courseRepository.findById(course.getId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }

    @Transactional
    public void deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        courseRepository.delete(course);
    }

    private CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .language(course.getLanguage())
                .iconUrl(course.getIconUrl())
                .description(course.getDescription())
                .lessons(course.getLessons() != null ? course.getLessons().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }

    private LessonDTO convertToDTO(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .order(lesson.getOrder())
                .iconUrl(lesson.getIconUrl())
                .xpReward(lesson.getXpReward())
                .exercises(lesson.getExercises() != null ? lesson.getExercises().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }

    private ExerciseDTO convertToDTO(Exercise exercise) {
        ExerciseDTO dto = ExerciseDTO.builder()
                .id(exercise.getId())
                .question(exercise.getQuestion())
                .hint(exercise.getHint())
                .xpReward(exercise.getXpReward())
                .heartsCost(exercise.getHeartsCost())
                .build();

        switch (exercise) {
            case TranslationExercise translationExercise -> {
                dto.setType("TRANSLATION");
                dto.setCorrectAnswer(translationExercise.getCorrectAnswer());
            }
            case MultipleChoiceExercise multipleChoiceExercise -> {
                dto.setType("MULTIPLE_CHOICE");
                dto.setOptions(multipleChoiceExercise.getOptions());
                dto.setCorrectOptionIndex(multipleChoiceExercise.getCorrectOptionIndex());
            }
            case MatchingExercise matchingExercise -> {
                dto.setType("MATCHING");
                dto.setPairs(matchingExercise.getPairs());
            }
            default -> {
            }
        }

        return dto;
    }
}
