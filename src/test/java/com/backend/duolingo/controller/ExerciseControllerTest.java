package com.backend.duolingo.controller;

import com.backend.duolingo.dto.ExerciseDTO;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.TranslationExercise;
import com.backend.duolingo.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExerciseControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    private UUID lessonId;
    private ExerciseDTO exerciseDTO1;
    private ExerciseDTO exerciseDTO2;
    private TranslationExercise exercise;

    @BeforeEach
    void setUp() {
        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(exerciseController).build();

        // Create test data
        lessonId = UUID.randomUUID();

        exerciseDTO1 = ExerciseDTO.builder()
                .id(UUID.randomUUID())
                .question("Translate 'Hello' to Spanish")
                .hint("It starts with 'H'")
                .order(1)
                .xpReward(10)
                .heartsCost(1)
                .type("translation")
                .correctAnswer("Hola")
                .build();

        exerciseDTO2 = ExerciseDTO.builder()
                .id(UUID.randomUUID())
                .question("Translate 'Goodbye' to Spanish")
                .hint("It starts with 'A'")
                .order(2)
                .xpReward(10)
                .heartsCost(1)
                .type("translation")
                .correctAnswer("Adiós")
                .build();

        exercise = TranslationExercise.builder()
                .id(UUID.randomUUID())
                .question("Translate 'Thank you' to Spanish")
                .hint("It starts with 'G'")
                .exerciseOrder(3)
                .xpReward(10)
                .heartsCost(1)
                .correctAnswer("Gracias")
                .correctOptionIndex(0)
                .build();
    }

    @Test
    void getExercisesByLesson() throws Exception {
        // Arrange
        List<Exercise> exercises = Collections.singletonList(exercise);

        when(exerciseService.getExercisesByLesson(lessonId)).thenReturn(exercises);

        // Act & Assert
        mockMvc.perform(get("/api/exercises/lesson/" + lessonId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value(exercise.getQuestion()))
                .andExpect(jsonPath("$[0].type").value("translation"));
    }

    @Test
    void createExercise() throws Exception {
        // Arrange
        when(exerciseService.createExercise(eq(lessonId), any(Exercise.class))).thenReturn(exercise);

        // Act & Assert
        mockMvc.perform(post("/api/exercises/lesson/" + lessonId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exerciseDTO1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.question").value(exercise.getQuestion()));
    }
}
