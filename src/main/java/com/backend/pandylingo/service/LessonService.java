package com.backend.pandylingo.service;

import com.backend.pandylingo.controller.ExerciseController;
import com.backend.pandylingo.dto.exercise.ExerciseDTO;
import com.backend.pandylingo.dto.lesson.CreateLessonRequest;
import com.backend.pandylingo.dto.lesson.LessonDTO;
import com.backend.pandylingo.exception.*;
import com.backend.pandylingo.model.*;
import com.backend.pandylingo.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final AppStatsService appStatsService;

    @Transactional(readOnly = true)
    public LessonDTO getLessonById(UUID lessonId) {
        try {
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException(Lesson.class, lessonId));
            return convertToDTO(lesson);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve lesson");
        }
    }

    @Transactional(readOnly = true)
    public List<LessonDTO>  getLessonsByLanguageAndDifficulty(Language language, Difficulty difficulty) {
        try {
            List<Lesson> lessons = lessonRepository.findAllByLanguageAndDifficulty(language, difficulty);
            return lessons.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve lesson");
        }
    }


    @Transactional(readOnly = true)
    public int countExercisesInLesson(UUID lessonId) {
        try {
            Optional<Lesson> lesson = lessonRepository.findById(lessonId);
            if (lesson.isPresent()) {
                return lesson.get().getExercises().size();
            }
            throw new NotFoundException("Lesson not found");
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to count exercises");
        }
    }

    @Transactional
    public Lesson createLesson(CreateLessonRequest request) {
        try {
            Lesson newLesson = new Lesson();
            newLesson.setXpReward(request.getXpReward());
            newLesson.setTitle(request.getTitle());
            newLesson.setLanguage(request.getLanguage());
            newLesson.setDifficulty(request.getDifficulty());

            Lesson savedLesson = lessonRepository.save(newLesson);

            appStatsService.incrementLessonsCount();
            return savedLesson;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid lesson data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to create lesson");
        }
    }

    @Transactional
    public Lesson updateLesson(Lesson lesson) {
        try {
            Lesson existing = lessonRepository.findById(lesson.getId())
                    .orElseThrow(() -> new NotFoundException(Lesson.class, lesson.getId()));

            // Check for title conflicts in the same course
            if (!existing.getTitle().equals(lesson.getTitle())) {
                if (lessonRepository.existsByTitle(
                        lesson.getTitle())) {
                    throw new ConflictException("Lesson with this title already exists in the course");
                }
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