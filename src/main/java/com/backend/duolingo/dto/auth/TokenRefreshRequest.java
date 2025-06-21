package com.backend.duolingo.dto.auth;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}