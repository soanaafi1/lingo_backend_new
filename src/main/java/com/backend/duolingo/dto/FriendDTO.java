package com.backend.duolingo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private UUID id;
    private UUID userId;
    private String fullName;
    private int xpPoints;
    private int streak;
    private boolean accepted;
    private boolean isPending;
    private boolean isIncoming;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}