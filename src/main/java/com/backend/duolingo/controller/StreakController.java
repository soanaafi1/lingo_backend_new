package com.backend.duolingo.controller;

import com.backend.duolingo.dto.StreakDTO;
import com.backend.duolingo.model.User;
import com.backend.duolingo.repository.UserRepository;
import com.backend.duolingo.security.JwtUtils;
import com.backend.duolingo.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/streak")
@RequiredArgsConstructor
public class StreakController {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping
    public ResponseEntity<StreakDTO> getStreak(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        
        LocalDate today = LocalDate.now();
        LocalDate lastUpdate = user.getLastStreakUpdate().toLocalDate();
        boolean practicedToday = lastUpdate.equals(today);
        
        StreakDTO streakDTO = StreakDTO.builder()
                .currentStreak(user.getStreak())
                .lastStreakUpdate(user.getLastStreakUpdate())
                .streakFreezeCount(user.getStreakFreezeCount())
                .practicedToday(practicedToday)
                .build();
        
        return ResponseEntity.ok(streakDTO);
    }
    
    @PostMapping("/freeze")
    public ResponseEntity<?> useStreakFreeze(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        
        // Check if user has streak freezes
        if (user.getStreakFreezeCount() <= 0) {
            return ResponseEntity.badRequest().body("No streak freezes available");
        }
        
        // Use a streak freeze
        user.setStreakFreezeCount(user.getStreakFreezeCount() - 1);
        
        // Update last streak update to today to maintain streak
        user.setLastStreakUpdate(LocalDateTime.now());
        
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/buy-freeze")
    public ResponseEntity<?> buyStreakFreeze(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        
        // Cost of a streak freeze in XP
        final int FREEZE_COST = 200;
        
        // Check if user has enough XP
        if (user.getXpPoints() < FREEZE_COST) {
            return ResponseEntity.badRequest().body("Not enough XP to buy a streak freeze");
        }
        
        // Deduct XP and add streak freeze
        user.setXpPoints(user.getXpPoints() - FREEZE_COST);
        user.setStreakFreezeCount(user.getStreakFreezeCount() + 1);
        
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
    
    private User getUserFromToken(String token) {
        String jwt = token.substring(7);
        String username = jwtUtils.extractUsername(jwt);
        return (User) userDetailsService.loadUserByUsername(username);
    }
}