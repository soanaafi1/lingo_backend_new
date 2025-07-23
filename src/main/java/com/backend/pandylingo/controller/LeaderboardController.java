package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.user.LeaderboardEntryDTO;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.model.UserProfile;
import com.backend.pandylingo.repository.UserProfileRepository;
import com.backend.pandylingo.security.JwtUtils;
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
    private final UserProfileRepository userProfileRepository;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "10") int limit) {

        User currentUser = jwtUtils.getUserFromAccessToken(token);
        UUID currentUserId = currentUser.getId();

        // Get top users by XP
        List<UserProfile> topUsers = userProfileRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "xpPoints"))
        ).getContent();

        // Convert to DTOs with rank
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            UserProfile userProfile = topUsers.get(i);
            leaderboard.add(LeaderboardEntryDTO.builder()
                    .userId(userProfile.getUser().getId())
                    .fullName(userProfile.getUser().getFullName())
                    .avatarUrl(userProfile.getAvatarUrl())
                    .xpPoints(userProfile.getXpPoints())
                    .streak(userProfile.getStreak())
                    .rank(i + 1)
                    .isCurrentUser(userProfile.getUser().getId().equals(currentUserId))
                    .build());
        }

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/me")
    public ResponseEntity<List<LeaderboardEntryDTO>> getUserLeaderboardPosition(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "5") int range) {
        
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        UUID currentUserId = currentUser.getId();
        
        // Get all users sorted by XP
        List<UserProfile> allUsers = userProfileRepository.findAll(
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
        List<UserProfile> usersInRange = allUsers.subList(startIndex, endIndex);
        
        // Convert to DTOs with rank
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        for (int i = 0; i < usersInRange.size(); i++) {
            UserProfile user = usersInRange.get(i);
            leaderboard.add(LeaderboardEntryDTO.builder()
                    .userId(user.getId())
                    .fullName(user.getUser().getFullName())
                    .xpPoints(user.getXpPoints())
                    .streak(user.getStreak())
                    .rank(startIndex + i + 1)
                    .isCurrentUser(user.getId().equals(currentUserId))
                    .build());
        }
        
        return ResponseEntity.ok(leaderboard);
    }
}