package com.backend.pandylingo.dto.auth;

import com.backend.pandylingo.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class LoginResponse {
    private UUID userId;
    private String accessToken;
    private String refreshToken;
    private Role role;
    private List<String> authorities;
}
