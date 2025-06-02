package com.backend.duolingo.controller;

import com.backend.duolingo.dto.LessonProgress;
import com.backend.duolingo.dto.UserProgressResponse;
import com.backend.duolingo.model.Exercise;
import com.backend.duolingo.model.TranslationExercise;
import com.backend.duolingo.model.User;
import com.backend.duolingo.model.UserProgress;
import com.backend.duolingo.security.JwtUtils;
import com.backend.duolingo.security.UserDetailsServiceImpl;
import com.backend.duolingo.service.ProgressService;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProgressControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProgressService progressService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private ProgressController progressController;

    private UUID userId;
    private UUID exerciseId;
    private UUID lessonId;
    private User user;
    private Exercise exercise;
    private UserProgress userProgress;
    private UserProgressResponse userProgressResponse;
    private LessonProgress lessonProgress;
    private String token;

    @BeforeEach
    void setUp() {
        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(progressController).build();

        // Create test data
        userId = UUID.randomUUID();
        exerciseId = UUID.randomUUID();
        lessonId = UUID.randomUUID();
        token = "Bearer test.jwt.token";

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        exercise = TranslationExercise.builder()
                .id(exerciseId)
                .question("Translate 'Hello' to Spanish")
                .hint("It starts with 'H'")
                .exerciseOrder(1)
                .xpReward(10)
                .heartsCost(1)
                .correctAnswer("Hola")
                .correctOptionIndex(0)
                .build();

        userProgress = new UserProgress();
        userProgress.setId(UUID.randomUUID());
        userProgress.setUser(user);
        userProgress.setExercise(exercise);
        userProgress.setCompleted(true);
        userProgress.setCorrect(true);
        userProgress.setCompletedAt(LocalDateTime.now());
        userProgress.setUserAnswer("Hola");
        userProgress.setXpEarned(10);
        userProgress.setHeartsUsed(0);

        userProgressResponse = UserProgressResponse.builder()
                .id(userProgress.getId())
                .exerciseId(exerciseId)
                .exerciseType("TranslationExercise")
                .question(exercise.getQuestion())
                .completed(true)
                .correct(true)
                .completedAt(userProgress.getCompletedAt())
                .userAnswer("Hola")
                .xpEarned(10)
                .heartsUsed(0)
                .build();

        lessonProgress = new LessonProgress();
        lessonProgress.setLessonId(lessonId);
        lessonProgress.setTotalExercises(5);
        lessonProgress.setCompletedExercises(3);
        lessonProgress.setCorrectExercises(2);
        lessonProgress.setPercentComplete(60);
        lessonProgress.setPercentCorrect(67);
    }

    @Test
    void submitExercise() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("answer", "Hola");

        when(jwtUtils.extractUsername("test.jwt.token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);
        when(progressService.submitExercise(userId, exerciseId, "Hola")).thenReturn(userProgress);

        // Act & Assert
        mockMvc.perform(post("/api/progress/submit?exerciseId=" + exerciseId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseId").value(exerciseId.toString()))
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.xpEarned").value(10));
    }

    @Test
    void getUserProgress() throws Exception {
        // Arrange
        List<UserProgress> progressList = Collections.singletonList(userProgress);

        when(jwtUtils.extractUsername("test.jwt.token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);
        when(progressService.getUserProgress(userId)).thenReturn(progressList);

        // Act & Assert
        mockMvc.perform(get("/api/progress")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exerciseId").value(exerciseId.toString()))
                .andExpect(jsonPath("$[0].correct").value(true))
                .andExpect(jsonPath("$[0].xpEarned").value(10));
    }

    @Test
    void getLessonProgress() throws Exception {
        // Arrange
        when(jwtUtils.extractUsername("test.jwt.token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);
        when(progressService.getLessonProgress(userId, lessonId)).thenReturn(lessonProgress);

        // Act & Assert
        mockMvc.perform(get("/api/progress/lesson/" + lessonId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value(lessonId.toString()))
                .andExpect(jsonPath("$.totalExercises").value(5))
                .andExpect(jsonPath("$.completedExercises").value(3))
                .andExpect(jsonPath("$.percentComplete").value(60));
    }
}