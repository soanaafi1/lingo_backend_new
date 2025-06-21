package com.backend.duolingo.service;

import com.backend.duolingo.dto.*;
import com.backend.duolingo.exception.*;
import com.backend.duolingo.model.*;
import com.backend.duolingo.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final AppStatsService appStatsService;

    @Transactional(readOnly = true)
    public List<GetAllCoursesResponse> getAllCourses() {
        try {
            return courseRepository.findAllWithoutLessons().stream()
                    .map(this::convertToCourseList)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve courses", ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseWithLessons(UUID courseId) {
        try {
            Course course = courseRepository.findByIdWithLessons(courseId)
                    .orElseThrow(() -> new NotFoundException(Course.class, courseId));
            return convertToDTO(course);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve course details", ex.getMessage());
        }
    }

    @Transactional
    public String createCourse(CreateCourseRequest request) {
        try {
            if (request.getCourseName() == null || request.getCourseName().isBlank()) {
                throw new BadRequestException("Course name is required");
            }

            if (courseRepository.existsByName(request.getCourseName())) {
                throw new ConflictException("Course with this name already exists");
            }

            Course course = new Course();
            course.setName(request.getCourseName());
            course.setDescription(request.getDescription());
            course.setSourceLanguage(request.getSourceLanguage());
            course.setTargetLanguage(request.getTargetLanguage());
            course.setDifficulty(request.getDifficulty());

            courseRepository.save(course);
            appStatsService.incrementCoursesCount();

            return "Course created successfully";
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid course data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create course", ex.getMessage());
        }
    }

    @Transactional
    public CourseDTO updateCourse(Course course) {
        try {
            if (!courseRepository.existsById(course.getId())) {
                throw new NotFoundException(Course.class, course.getId());
            }

            Course updatedCourse = courseRepository.save(course);
            return convertToDTO(updatedCourse);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid course data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update course", ex.getMessage());
        }
    }

    @Transactional
    public void deleteCourse(UUID courseId) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new NotFoundException(Course.class, courseId));

            courseRepository.delete(course);
            appStatsService.decrementCourses();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to delete course", ex.getMessage());
        }
    }

    private GetAllCoursesResponse convertToCourseList(Course course) {
        return GetAllCoursesResponse.builder()
                .id(course.getId())
                .courseName(course.getName())
                .sourceLanguage(course.getSourceLanguage())
                .targetLanguage(course.getTargetLanguage())
                .description(course.getDescription())
                .difficulty(course.getDifficulty())
                .build();
    }

    private CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .sourceLanguage(course.getSourceLanguage())
                .targetLanguage(course.getTargetLanguage())
                .description(course.getDescription())
                .difficulty(course.getDifficulty())
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
