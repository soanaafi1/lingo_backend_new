package com.backend.duolingo.dto;

import com.backend.duolingo.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private String token;
    private UUID id;
    private String username;
    private String email;
    private int xpPoints;
    private int streak;
    private int hearts;
    private Role role;
    private List<String> authorities;
    private String avatarUrl;
}
