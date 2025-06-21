package com.backend.duolingo.controller;

import com.backend.duolingo.dto.exercise.ExerciseDTO;
import com.backend.duolingo.exception.InternalServerErrorException;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.MatchingExercise;
import com.backend.duolingo.model.MultipleChoiceExercise;
import com.backend.duolingo.model.TranslationExercise;
import com.backend.duolingo.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByLesson(@PathVariable UUID lessonId) {
        try {
            List<Exercise> exercises = exerciseService.getExercisesByLesson(lessonId);
            List<ExerciseDTO> exerciseDTOs = exercises.stream()
                    .map(ExerciseController::getExerciseDTO)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(exerciseDTOs);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve exercises", ex.getMessage());
        }
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
                .heartsCost(exercise.getHeartsCost())
                .correctAnswer(exercise.getCorrectAnswer());

        switch (exercise) {
            case TranslationExercise translationExercise -> builder.type("translation");
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

    public static Exercise getExercise(ExerciseDTO dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Exercise type is required");
        }

        switch (dto.getType().toLowerCase()) {
            case "translation":
                return TranslationExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .exerciseOrder(dto.getOrder())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .correctAnswer(dto.getCorrectAnswer())
                        .correctOptionIndex(0) // Default value for translation exercises
                        .build();

            case "multiple_choice":
                if (dto.getOptions() == null || dto.getOptions().isEmpty()) {
                    throw new IllegalArgumentException("Options are required for multiple choice exercises");
                }
                if (dto.getCorrectOptionIndex() == null) {
                    throw new IllegalArgumentException("Correct option index is required");
                }

                return MultipleChoiceExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .exerciseOrder(dto.getOrder())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .options(dto.getOptions())
                        .correctOptionIndex(dto.getCorrectOptionIndex())
                        .correctAnswer(dto.getOptions().get(dto.getCorrectOptionIndex()))
                        .build();

            case "matching":
                if (dto.getPairs() == null || dto.getPairs().isEmpty()) {
                    throw new IllegalArgumentException("Pairs are required for matching exercises");
                }

                return MatchingExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .exerciseOrder(dto.getOrder())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .pairs(dto.getPairs())
                        .correctAnswer(String.join(",", dto.getPairs().entrySet().stream()
                                .map(e -> e.getKey() + ":" + e.getValue())
                                .toList()))
                        .build();

            default:
                throw new IllegalArgumentException("Invalid exercise type: " + dto.getType());
        }
    }
}
