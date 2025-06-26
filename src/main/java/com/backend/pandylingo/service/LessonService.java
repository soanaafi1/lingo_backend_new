package com.backend.pandylingo.service;

import com.backend.pandylingo.controller.ExerciseController;
import com.backend.pandylingo.dto.exercise.ExerciseDTO;
import com.backend.pandylingo.dto.lesson.LessonDTO;
import com.backend.pandylingo.exception.*;
import com.backend.pandylingo.model.Course;
import com.backend.pandylingo.model.Exercise;
import com.backend.pandylingo.model.Lesson;
import com.backend.pandylingo.repository.CourseRepository;
import com.backend.pandylingo.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final AppStatsService appStatsService;

    @Transactional(readOnly = true)
    public Lesson getLessonById(UUID lessonId) {
        try {
            return lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new NotFoundException(Lesson.class, lessonId));
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve lesson");
        }
    }

    @Transactional(readOnly = true)
    public int countExercisesInLesson(UUID lessonId) {
        try {
            Lesson lesson = getLessonById(lessonId);
            return lesson.getExercises().size();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to count exercises");
        }
    }

    @Transactional
    public Lesson createLesson(UUID courseId, Lesson lesson) {
        try {
            validateLesson(lesson);
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new NotFoundException(Course.class, courseId));

            if (lessonRepository.existsByTitleAndCourseId(lesson.getTitle(), courseId)) {
                throw new ConflictException("Lesson with this title already exists in the course");
            }

            lesson.setCourse(course);
            Lesson savedLesson = lessonRepository.save(lesson);
            appStatsService.incrementLessonsCount();
            return savedLesson;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid lesson data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create lesson");
        }
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> getLessonsByCourse(UUID courseId) {
        try {
            if (!courseRepository.existsById(courseId)) {
                throw new NotFoundException(Course.class, courseId);
            }

            List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
            return lessons.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve lessons");
        }
    }

    @Transactional
    public Lesson updateLesson(Lesson lesson) {
        try {
            validateLesson(lesson);
            Lesson existing = lessonRepository.findById(lesson.getId())
                    .orElseThrow(() -> new NotFoundException(Lesson.class, lesson.getId()));

            // Check for title conflicts in the same course
            if (!existing.getTitle().equals(lesson.getTitle())) {
                if (lessonRepository.existsByTitleAndCourseId(
                        lesson.getTitle(),
                        existing.getCourse().getId()
                )) {
                    throw new ConflictException("Lesson with this title already exists in the course");
                }
            }

            // Preserve the original course if not changed
            if (lesson.getCourse() == null) {
                lesson.setCourse(existing.getCourse());
            } else if (!lesson.getCourse().getId().equals(existing.getCourse().getId())) {
                // Verify new course exists
                courseRepository.findById(lesson.getCourse().getId())
                        .orElseThrow(() -> new NotFoundException(Course.class, lesson.getCourse().getId()));
            }

            return lessonRepository.save(lesson);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConflictException("Lesson was modified by another user. Refresh and try again.");
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid lesson data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update lesson");
        }
    }

    @Transactional
    public void deleteLesson(UUID lessonId) {
        try {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new NotFoundException(Lesson.class, lessonId));

            lessonRepository.delete(lesson);
            appStatsService.decrementLessons();
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to delete lesson");
        }
    }

    // Helper methods
    private void validateLesson(Lesson lesson) {
        if (lesson.getTitle() == null || lesson.getTitle().isBlank()) {
            throw new BadRequestException("Lesson title is required");
        }
        if (lesson.getXpReward() < 0) {
            throw new BadRequestException("XP reward cannot be negative");
        }
    }

    public LessonDTO convertToDTO(Lesson lesson) {
        List<ExerciseDTO> exerciseDTOs = lesson.getExercises().stream()
                .map(this::convertExerciseToDTO)
                .collect(Collectors.toList());

        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .xpReward(lesson.getXpReward())
                .exercises(exerciseDTOs)
                .build();
    }

    private ExerciseDTO convertExerciseToDTO(Exercise exercise) {
        return ExerciseController.getExerciseDTO(exercise);
    }
}