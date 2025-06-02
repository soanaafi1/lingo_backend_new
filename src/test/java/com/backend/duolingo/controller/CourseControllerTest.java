package com.backend.duolingo.controller;

import com.backend.duolingo.dto.CourseDTO;
import com.backend.duolingo.model.Course;
import com.backend.duolingo.service.CourseService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private CourseDTO courseDTO1;
    private CourseDTO courseDTO2;
    private Course course;

    @BeforeEach
    void setUp() {
        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        // Create test courses
        courseDTO1 = new CourseDTO();
        courseDTO1.setId(UUID.randomUUID());
        courseDTO1.setName("Spanish for English Speakers");
        courseDTO1.setDescription("Learn Spanish from English");
        courseDTO1.setLanguage("Spanish");
        courseDTO1.setIconUrl("https://example.com/spanish.jpg");

        courseDTO2 = new CourseDTO();
        courseDTO2.setId(UUID.randomUUID());
        courseDTO2.setName("French for English Speakers");
        courseDTO2.setDescription("Learn French from English");
        courseDTO2.setLanguage("French");
        courseDTO2.setIconUrl("https://example.com/french.jpg");

        course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("German for English Speakers");
        course.setDescription("Learn German from English");
        course.setLanguage("German");
        course.setIconUrl("https://example.com/german.jpg");
    }

    @Test
    void getAllCourses() throws Exception {
        // Arrange
        List<CourseDTO> courses = Arrays.asList(courseDTO1, courseDTO2);
        when(courseService.getAllCourses()).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(courseDTO1.getName()))
                .andExpect(jsonPath("$[0].language").value(courseDTO1.getLanguage()))
                .andExpect(jsonPath("$[1].name").value(courseDTO2.getName()))
                .andExpect(jsonPath("$[1].language").value(courseDTO2.getLanguage()));
    }

    @Test
    void getCourseById() throws Exception {
        // Arrange
        when(courseService.getCourseWithLessons(courseDTO1.getId())).thenReturn(courseDTO1);

        // Act & Assert
        mockMvc.perform(get("/api/courses/" + courseDTO1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(courseDTO1.getName()))
                .andExpect(jsonPath("$.language").value(courseDTO1.getLanguage()));
    }

    @Test
    void createCourse() throws Exception {
        // Arrange
        when(courseService.createCourse(any(Course.class))).thenReturn(courseDTO1);

        // Act & Assert
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(courseDTO1.getName()))
                .andExpect(jsonPath("$.language").value(courseDTO1.getLanguage()));
    }

    @Test
    void getCoursesByLanguage() throws Exception {
        // Arrange
        List<CourseDTO> courses = Collections.singletonList(courseDTO1);
        when(courseService.getCoursesByLanguage("Spanish")).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/courses/language/Spanish")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(courseDTO1.getName()))
                .andExpect(jsonPath("$[0].language").value(courseDTO1.getLanguage()));
    }
}
