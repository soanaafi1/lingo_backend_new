package com.backend.duolingo.controller;

import com.backend.duolingo.dto.LeaderboardEntryDTO;
import com.backend.duolingo.model.User;
import com.backend.duolingo.repository.UserRepository;
import com.backend.duolingo.security.JwtUtils;
import com.backend.duolingo.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "10") int limit) {
        
        User currentUser = getUserFromToken(token);
        UUID currentUserId = currentUser.getId();
        
        // Get top users by XP
        List<User> topUsers = userRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "xpPoints"))
        ).getContent();
        
        // Convert to DTOs with rank
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);
            leaderboard.add(LeaderboardEntryDTO.builder()
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .xpPoints(user.getXpPoints())
                    .streak(user.getStreak())
                    .rank(i + 1)
                    .isCurrentUser(user.getId().equals(currentUserId))
                    .build());
        }
        
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/me")
    public ResponseEntity<List<LeaderboardEntryDTO>> getUserLeaderboardPosition(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "5") int range) {
        
        User currentUser = getUserFromToken(token);
        UUID currentUserId = currentUser.getId();
        
        // Get all users sorted by XP
        List<User> allUsers = userRepository.findAll(
                Sort.by(Sort.Direction.DESC, "xpPoints")
        );
        
        // Find current user's position
        int currentUserPosition = -1;
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getId().equals(currentUserId)) {
                currentUserPosition = i;
                break;
            }
        }
        
        if (currentUserPosition == -1) {
            return ResponseEntity.notFound().build();
        }
        
        // Calculate range
        int startIndex = Math.max(0, currentUserPosition - range);
        int endIndex = Math.min(allUsers.size(), currentUserPosition + range + 1);
        
        // Get users in range
        List<User> usersInRange = allUsers.subList(startIndex, endIndex);
        
        // Convert to DTOs with rank
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        for (int i = 0; i < usersInRange.size(); i++) {
            User user = usersInRange.get(i);
            leaderboard.add(LeaderboardEntryDTO.builder()
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .xpPoints(user.getXpPoints())
                    .streak(user.getStreak())
                    .rank(startIndex + i + 1)
                    .isCurrentUser(user.getId().equals(currentUserId))
                    .build());
        }
        
        return ResponseEntity.ok(leaderboard);
    }
    
    private User getUserFromToken(String token) {
        String jwt = token.substring(7);
        UUID userId = jwtUtils.getUserIdFromToken(jwt);
        return (User) userDetailsService.loadUserById(userId);
    }
}