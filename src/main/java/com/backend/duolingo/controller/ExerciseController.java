package com.backend.duolingo.controller;

import com.backend.duolingo.dto.ExerciseDTO;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.MatchingExercise;
import com.backend.duolingo.model.MultipleChoiceExercise;
import com.backend.duolingo.model.TranslationExercise;
import com.backend.duolingo.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByLesson(@PathVariable UUID lessonId) {
        List<Exercise> exercises = exerciseService.getExercisesByLesson(lessonId);
        List<ExerciseDTO> exerciseDTOs = exercises.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(exerciseDTOs);
    }

    private ExerciseDTO convertToDTO(Exercise exercise) {
        return getExerciseDTO(exercise);
    }

    public static ExerciseDTO getExerciseDTO(Exercise exercise) {
        ExerciseDTO.ExerciseDTOBuilder builder = ExerciseDTO.builder()
                .id(exercise.getId())
                .question(exercise.getQuestion())
                .hint(exercise.getHint())
                .order(exercise.getExerciseOrder())
                .xpReward(exercise.getXpReward())
                .heartsCost(exercise.getHeartsCost());

        switch (exercise) {
            case TranslationExercise translationExercise -> builder.type("translation")
                    .correctAnswer(translationExercise.getCorrectAnswer());
            case MultipleChoiceExercise multipleChoiceExercise -> builder.type("multiple_choice")
                    .options(multipleChoiceExercise.getOptions())
                    .correctOptionIndex(multipleChoiceExercise.getCorrectOptionIndex());
            case MatchingExercise matchingExercise -> builder.type("matching")
                    .pairs(matchingExercise.getPairs());
            default -> {
            }
        }

        return builder.build();
    }

    @PostMapping("/lesson/{lessonId}")
    public ResponseEntity<Exercise> createExercise(
            @PathVariable UUID lessonId,
            @RequestBody ExerciseDTO exerciseDTO) {
        Exercise exercise = convertToExercise(exerciseDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exerciseService.createExercise(lessonId, exercise));
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
}
