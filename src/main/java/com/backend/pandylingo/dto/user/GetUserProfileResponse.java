package com.backend.pandylingo.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserProfileResponse {
    private String name;
    private String email;
    private String avatarUrl;
//  private UserLevel userLevel;
    private int streak;
    private int totalXp;
    private int lessonsCompleted;

//  private List<Map<Language, PercentComplete>> languageCompletions;
}
