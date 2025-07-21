package com.backend.pandylingo.dto.friendship;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AddFriendRequest {
    @NotBlank
    private UUID friendID;
}
