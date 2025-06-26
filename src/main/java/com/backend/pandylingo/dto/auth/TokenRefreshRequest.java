package com.backend.pandylingo.dto.auth;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}