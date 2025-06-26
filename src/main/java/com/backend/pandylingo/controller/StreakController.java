package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.user.StreakDTO;
import com.backend.pandylingo.exception.NotFoundException;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.repository.UserRepository;
import com.backend.pandylingo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/streak")
@RequiredArgsConstructor
public class StreakController {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<StreakDTO> getStreak(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = userRepository.findByIdWithProfile(jwtUtils.getUserIdFromAccessToken(token));

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userOptional.get();

        LocalDate today = LocalDate.now();
        LocalDate lastUpdate = user.getUserProfile().getLastStreakUpdate().toLocalDate();
        boolean practicedToday = lastUpdate.equals(today);
        
        StreakDTO streakDTO = StreakDTO.builder()
                .currentStreak(user.getUserProfile().getStreak())
                .lastStreakUpdate(user.getUserProfile().getLastStreakUpdate())
                .streakFreezeCount(user.getUserProfile().getStreakFreezeCount())
                .practicedToday(practicedToday)
                .build();
        
        return ResponseEntity.ok(streakDTO);
    }
    
    @PostMapping("/freeze")
    public ResponseEntity<?> useStreakFreeze(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = userRepository.findByIdWithProfile(jwtUtils.getUserIdFromAccessToken(token));

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = userOptional.get();

        // Check if user has streak freezes
        if (user.getUserProfile().getStreakFreezeCount() <= 0) {
            return ResponseEntity.badRequest().body("No streak freezes available");
        }
        
        // Use a streak freeze
        user.getUserProfile().setStreakFreezeCount(user.getUserProfile().getStreakFreezeCount() - 1);
        
        // Update last streak update to today to maintain streak
        user.getUserProfile().setLastStreakUpdate(LocalDateTime.now());
        
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/buy-freeze")
    public ResponseEntity<?> buyStreakFreeze(@RequestHeader("Authorization") String token) {
        User user = jwtUtils.getUserFromAccessToken(token);
        
        // Cost of a streak freeze in XP
        final int FREEZE_COST = 200;
        
        // Check if user has enough XP
        if (user.getUserProfile().getXpPoints() < FREEZE_COST) {
            return ResponseEntity.badRequest().body("Not enough XP to buy a streak freeze");
        }
        
        // Deduct XP and add streak freeze
        user.getUserProfile().setXpPoints(user.getUserProfile().getXpPoints() - FREEZE_COST);
        user.getUserProfile().setStreakFreezeCount(user.getUserProfile().getStreakFreezeCount() + 1);
        
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
}