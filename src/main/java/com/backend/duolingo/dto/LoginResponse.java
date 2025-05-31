package com.backend.duolingo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private int xpPoints;
}