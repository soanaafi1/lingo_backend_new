package com.backend.duolingo.service;

import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.Lesson;
import com.backend.duolingo.repository.ExerciseRepository;
import com.backend.duolingo.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public Exercise createExercise(UUID lessonId, Exercise exercise) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));
        exercise.setLesson(lesson);
        return exerciseRepository.save(exercise);
    }

    @Transactional(readOnly = true)
    public List<Exercise> getExercisesByLesson(UUID lessonId) {
        return exerciseRepository.findByLessonId(lessonId);
    }

    @Transactional
    public Exercise updateExercise(Exercise exercise) {
        // Check if exercise exists
        exerciseRepository.findById(exercise.getId())
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void deleteExercise(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        exerciseRepository.delete(exercise);
    }
}
