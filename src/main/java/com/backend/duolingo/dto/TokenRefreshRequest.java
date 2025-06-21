package com.backend.duolingo.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}