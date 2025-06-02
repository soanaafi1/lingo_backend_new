package com.backend.duolingo.controller;

import com.backend.duolingo.dto.LessonDTO;
import com.backend.duolingo.model.Lesson;
import com.backend.duolingo.service.LessonService;
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
public class LessonControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    private UUID courseId;
    private LessonDTO lessonDTO1;
    private LessonDTO lessonDTO2;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController).build();

        // Create test data
        courseId = UUID.randomUUID();

        lessonDTO1 = new LessonDTO();
        lessonDTO1.setId(UUID.randomUUID());
        lessonDTO1.setTitle("Basics 1");
        lessonDTO1.setIconUrl("https://example.com/basics1.jpg");
        lessonDTO1.setOrder(1);
        lessonDTO1.setXpReward(10);

        lessonDTO2 = new LessonDTO();
        lessonDTO2.setId(UUID.randomUUID());
        lessonDTO2.setTitle("Basics 2");
        lessonDTO2.setIconUrl("https://example.com/basics2.jpg");
        lessonDTO2.setOrder(2);
        lessonDTO2.setXpReward(15);

        lesson = new Lesson();
        lesson.setId(UUID.randomUUID());
        lesson.setTitle("Basics 3");
        lesson.setIconUrl("https://example.com/basics3.jpg");
        lesson.setOrder(3);
        lesson.setXpReward(20);
    }

    @Test
    void getLessonsByCourse() throws Exception {
        // Arrange
        List<LessonDTO> lessons = Arrays.asList(lessonDTO1, lessonDTO2);
        when(lessonService.getLessonsByCourse(courseId)).thenReturn(lessons);

        // Act & Assert
        mockMvc.perform(get("/api/lessons/course/" + courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(lessonDTO1.getTitle()))
                .andExpect(jsonPath("$[0].order").value(lessonDTO1.getOrder()))
                .andExpect(jsonPath("$[1].title").value(lessonDTO2.getTitle()))
                .andExpect(jsonPath("$[1].order").value(lessonDTO2.getOrder()));
    }

    @Test
    void createLesson() throws Exception {
        // Arrange
        when(lessonService.createLesson(eq(courseId), any(Lesson.class))).thenReturn(lesson);
        when(lessonService.convertToDTO(lesson)).thenReturn(lessonDTO1);

        // Act & Assert
        mockMvc.perform(post("/api/lessons/course/" + courseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lesson)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(lessonDTO1.getTitle()))
                .andExpect(jsonPath("$.order").value(lessonDTO1.getOrder()));
    }
}
