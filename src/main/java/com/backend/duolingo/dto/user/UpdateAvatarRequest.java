package com.backend.duolingo.dto.user;

import lombok.Data;

@Data
public class UpdateAvatarRequest {
    private String avatarUrl;
}