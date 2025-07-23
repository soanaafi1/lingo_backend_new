package com.backend.pandylingo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
public class TokenRefreshResponse {
    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}