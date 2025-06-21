package com.backend.duolingo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private UUID userId;
    private String fullName;
    private int xpPoints;
    private int streak;
    private int rank;
    private boolean isCurrentUser;
}