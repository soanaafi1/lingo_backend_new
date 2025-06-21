package com.backend.duolingo.dto;

import com.backend.duolingo.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private UUID id;
    private String accessToken;
    private String refreshToken;
    private Role role;
    private List<String> authorities;
}
