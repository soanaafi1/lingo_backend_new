package com.backend.duolingo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserProgressResponse  {
    private UUID id;
    private UUID exerciseId;
    private String exerciseType;
    private String question;
    private boolean completed;
    private boolean correct;
    private LocalDateTime completedAt;
    private String userAnswer;
    private int xpEarned;
    private int heartsUsed;
}