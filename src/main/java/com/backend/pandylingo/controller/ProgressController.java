package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.progress.LessonProgress;
import com.backend.pandylingo.dto.progress.UserProgressResponse;
import com.backend.pandylingo.model.UserProgress;
import com.backend.pandylingo.security.JwtUtils;
import com.backend.pandylingo.security.UserDetailsServiceImpl;
import com.backend.pandylingo.service.LessonService;
import com.backend.pandylingo.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final LessonService lessonService; // Needed for lesson progress. Do that later

    @PostMapping("/submit")
    public ResponseEntity<UserProgressResponse> submitExercise(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID exerciseId,
            @RequestBody Map<String, String> request) {

        UUID userId = getUserIdFromToken(token);
        String answer = request.get("answer");

        UserProgress progress = progressService.submitExercise(userId, exerciseId, answer);
        return ResponseEntity.ok(mapToResponse(progress));
    }

    @GetMapping
    public ResponseEntity<List<UserProgressResponse>> getUserProgress(
            @RequestHeader("Authorization") String token) {
        UUID userId = getUserIdFromToken(token);
        List<UserProgress> progressList = progressService.getUserProgress(userId);

        List<UserProgressResponse> response = progressList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<LessonProgress> getLessonProgress(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID lessonId) {
        UUID userId = getUserIdFromToken(token);
        return ResponseEntity.ok(progressService.getLessonProgress(userId, lessonId));
    }

    private UUID getUserIdFromToken(String token) {
        String jwt = token.substring(7);
        return jwtUtils.getUserIdFromAccessToken(jwt);
    }

    private UserProgressResponse mapToResponse(UserProgress progress) {
        return UserProgressResponse.builder()
                .id(progress.getId())
                .exerciseId(progress.getExercise().getId())
                .exerciseType(progress.getExercise().getClass().getSimpleName())
                .question(progress.getExercise().getQuestion())
                .completed(progress.isCompleted())
                .correct(progress.isCorrect())
                .completedAt(progress.getCompletedAt())
                .userAnswer(progress.getUserAnswer())
                .xpEarned(progress.getXpEarned())
                .heartsUsed(progress.getHeartsUsed())
                .build();
    }
}
