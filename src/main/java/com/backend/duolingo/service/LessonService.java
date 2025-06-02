package com.backend.duolingo.service;

import com.backend.duolingo.dto.ExerciseDTO;
import com.backend.duolingo.dto.LessonDTO;
import com.backend.duolingo.model.Course;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.Lesson;
import com.backend.duolingo.repository.CourseRepository;
import com.backend.duolingo.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.backend.duolingo.controller.ExerciseController.getExerciseDTO;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Lesson createLesson(UUID courseId, Lesson lesson) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        lesson.setCourse(course);
        return lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> getLessonsByCourse(UUID courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        return lessons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LessonDTO convertToDTO(Lesson lesson) {
        List<ExerciseDTO> exerciseDTOs = lesson.getExercises().stream()
                .map(this::convertExerciseToDTO)
                .collect(Collectors.toList());

        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .order(lesson.getOrder())
                .iconUrl(lesson.getIconUrl())
                .xpReward(lesson.getXpReward())
                .exercises(exerciseDTOs)
                .build();
    }

    private ExerciseDTO convertExerciseToDTO(Exercise exercise) {
        return getExerciseDTO(exercise);
    }

    @Transactional
    public Lesson updateLesson(Lesson lesson) {
        // Check if lesson exists
        lessonRepository.findById(lesson.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        lessonRepository.delete(lesson);
    }
}
