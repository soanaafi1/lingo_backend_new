package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.exercise.ExerciseDTO;
import com.backend.pandylingo.exception.InternalServerErrorException;
import com.backend.pandylingo.model.Exercise;
import com.backend.pandylingo.model.MatchingExercise;
import com.backend.pandylingo.model.MultipleChoiceExercise;
import com.backend.pandylingo.model.TranslationExercise;
import com.backend.pandylingo.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<ExerciseDTO>> getExercisesByLessonId(@RequestParam UUID lessonId) {
        try {
            List<Exercise> exercises = exerciseService.getExercisesByLessonId(lessonId);
            List<ExerciseDTO> exerciseDTOs = exercises.stream()
                    .map(ExerciseController::getExerciseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(exerciseDTOs);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to retrieve exercises");
        }
    }

    // Converts an Exercise entity to an ExerciseDTO
    public static ExerciseDTO getExerciseDTO(Exercise exercise) {
        ExerciseDTO.ExerciseDTOBuilder builder = ExerciseDTO.builder()
                .id(exercise.getId())
                .question(exercise.getQuestion())
                .hint(exercise.getHint())
                .xpReward(exercise.getXpReward())
                .heartsCost(exercise.getHeartsCost())
                .correctAnswer(exercise.getCorrectAnswer());

        switch (exercise) {
            case TranslationExercise _ -> builder.type("translation");
            case MultipleChoiceExercise mce -> builder.type("multiple_choice")
                    .options(mce.getOptions());
            case MatchingExercise me -> builder.type("matching")
                    .pairs(me.getPairs());
            default -> throw new IllegalStateException("Unknown exercise type: " + exercise.getClass().getSimpleName());
        }

        return builder.build();
    }

    // Converts an ExerciseDTO to an Exercise entity
    public static Exercise getExerciseFromDTO(ExerciseDTO dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Exercise type is required");
        }
        if (dto.getCorrectAnswer() == null || dto.getCorrectAnswer().isBlank()) {
            throw new IllegalArgumentException("Correct answer is required for all exercise types");
        }

        return switch (dto.getType().toLowerCase()) {
            case "translation" -> TranslationExercise.builder()
                    .id(dto.getId())
                    .question(dto.getQuestion())
                    .hint(dto.getHint())
                    .xpReward(dto.getXpReward())
                    .heartsCost(dto.getHeartsCost())
                    .correctAnswer(dto.getCorrectAnswer())
                    .build();
            case "multiple_choice" -> {
                if (dto.getOptions() == null || dto.getOptions().isEmpty()) {
                    throw new IllegalArgumentException("Options are required for multiple choice exercises");
                }
                yield MultipleChoiceExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .options(dto.getOptions())
                        .correctAnswer(dto.getCorrectAnswer()) // Use the string correct answer
                        .build();
                // Correct answer for MC is now directly the string value of the correct option
            }
            case "matching" -> {
                if (dto.getPairs() == null || dto.getPairs().isEmpty()) {
                    throw new IllegalArgumentException("Pairs are required for matching exercises");
                }
                // For matching, correctAnswer in DTO should be the serialized string of pairs
                yield MatchingExercise.builder()
                        .id(dto.getId())
                        .question(dto.getQuestion())
                        .hint(dto.getHint())
                        .xpReward(dto.getXpReward())
                        .heartsCost(dto.getHeartsCost())
                        .pairs(dto.getPairs())
                        .correctAnswer(dto.getCorrectAnswer()) // Use the serialized string correct answer
                        .build();
                // For matching, correctAnswer in DTO should be the serialized string of pairs
            }
            default -> throw new IllegalArgumentException("Invalid exercise type: " + dto.getType());
        };
    }
}
