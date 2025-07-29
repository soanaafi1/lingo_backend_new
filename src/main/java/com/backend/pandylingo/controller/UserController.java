package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.user.GetUserProfileResponse;
import com.backend.pandylingo.dto.user.UpdateAvatarRequest;
import com.backend.pandylingo.exception.BadRequestException;
import com.backend.pandylingo.exception.InternalServerErrorException;
import com.backend.pandylingo.exception.NotFoundException;
import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.model.UserProfile;
import com.backend.pandylingo.repository.UserProfileRepository;
import com.backend.pandylingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;

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

    @GetMapping("/profile")
    public ResponseEntity<GetUserProfileResponse> getUserProfile(UUID userId) {
        Logger logger = Logger.getLogger(UserController.class.getName());
        try {
            Optional<User> optionalUser = userRepository.findByIdWithProfile(userId);

            if (optionalUser.isEmpty()) {
                throw new NotFoundException("User not found");
            }

            User user = optionalUser.get();
            logger.info(user.getUserProfile().getLanguageProficiencies().toString());

            GetUserProfileResponse response = GetUserProfileResponse.builder()
                    .email(user.getEmail())
                    .name(user.getFullName())
                    .avatarUrl(user.getUserProfile().getAvatarUrl())
                    .lessonsCompleted(25)
                    .totalXp(user.getUserProfile().getXpPoints())
                    .streak(user.getUserProfile().getStreak())
                    .build();

            return ResponseEntity.ok(response);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update avatar");
        }
    }

    @GetMapping("/languages")
    public ResponseEntity<Map<Language, Difficulty>> getUserLanguages(UUID userId) {
        try {
            Optional<User> optionalUser = userRepository.findByIdWithProfile(userId);

            if (optionalUser.isEmpty()) {
                throw new NotFoundException("User not found");
            }

            User user = optionalUser.get();

            Map<Language, Difficulty> languageDifficultyMap = user.getUserProfile().getLanguageProficiencies();
            return ResponseEntity.ok(languageDifficultyMap);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update avatar");
        }
    }


    @PatchMapping("/xp")
    public ResponseEntity<GetUserProfileResponse> updateXp(UUID userId, int xp) {
        Logger logger = Logger.getLogger(UserController.class.getName());
        try {
            Optional<User> optionalUser = userRepository.findByIdWithProfile(userId);

            if (optionalUser.isEmpty()) {
                throw new NotFoundException("User not found");
            }

            User user = optionalUser.get();
            logger.info(user.getUserProfile().getLanguageProficiencies().toString());

            UserProfile userProfile = user.getUserProfile();
            userProfile.setXpPoints(userProfile.getXpPoints() + xp);
            userRepository.save(user);

            GetUserProfileResponse response = GetUserProfileResponse.builder()
                    .email(user.getEmail())
                    .name(user.getFullName())
                    .avatarUrl(user.getUserProfile().getAvatarUrl())
                    .lessonsCompleted(25)
                    .totalXp(user.getUserProfile().getXpPoints())
                    .streak(user.getUserProfile().getStreak())
                    .build();

            return ResponseEntity.ok(response);
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to update avatar");
        }
    }

}
