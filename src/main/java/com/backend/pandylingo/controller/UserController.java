package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.user.UpdateAvatarRequest;
import com.backend.pandylingo.exception.BadRequestException;
import com.backend.pandylingo.exception.InternalServerErrorException;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PutMapping("/avatar")
    public ResponseEntity<String> updateAvatar(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateAvatarRequest request) {
        try {
            if (request.getAvatarUrl() == null || request.getAvatarUrl().isBlank()) {
                throw new BadRequestException("Avatar URL is required");
            }

            user.getUserProfile().setAvatarUrl(request.getAvatarUrl());
            userRepository.save(user);

            return ResponseEntity.ok("Avatar updated successfully");
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update avatar");
        }
    }
}
