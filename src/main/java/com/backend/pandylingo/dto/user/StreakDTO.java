package com.backend.pandylingo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreakDTO {
    private int currentStreak;
    private LocalDateTime lastStreakUpdate;
    private int streakFreezeCount;
    private boolean practicedToday;
}