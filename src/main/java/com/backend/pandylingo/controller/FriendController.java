package com.backend.pandylingo.controller;

import com.backend.pandylingo.dto.friendship.AddFriendRequest;
import com.backend.pandylingo.dto.user.FriendDTO;
import com.backend.pandylingo.exception.NotFoundException;
import com.backend.pandylingo.model.Friendship;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.repository.FriendshipRepository;
import com.backend.pandylingo.repository.UserRepository;
import com.backend.pandylingo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<FriendDTO>> getFriends(@RequestHeader("Authorization") String token) {
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        
        List<Friendship> friendships = friendshipRepository.findAllAcceptedByUser(currentUser);
        List<FriendDTO> friends = friendships.stream()
                .map(friendship -> mapToFriendDTO(friendship, currentUser))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendDTO>> getPendingFriends(@RequestHeader("Authorization") String token) {
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        
        // Get friend requests received
        List<Friendship> receivedRequests = friendshipRepository.findAllPendingByFriend(currentUser);
        List<FriendDTO> receivedFriends = receivedRequests.stream()
                .map(friendship -> mapToFriendDTO(friendship, currentUser))
                .collect(Collectors.toList());
        
        // Get friend requests sent
        List<Friendship> sentRequests = friendshipRepository.findAllPendingByUser(currentUser);
        List<FriendDTO> sentFriends = sentRequests.stream()
                .map(friendship -> mapToFriendDTO(friendship, currentUser))
                .toList();
        
        // Combine both lists
        receivedFriends.addAll(sentFriends);
        
        return ResponseEntity.ok(receivedFriends);
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody AddFriendRequest request) {
        
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        UUID friendID = request.getFriendID();
        
        // Find the friend user
        Optional<User> optionalFriend = userRepository.findByIdWithoutProfile(friendID);

        if (optionalFriend.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User friend = optionalFriend.get();
        
        // Check if they're already friends
        Optional<Friendship> existingFriendship = friendshipRepository.findByUserAndFriend(currentUser, Optional.of(friend));
        if (existingFriendship.isPresent()) {
            return ResponseEntity.badRequest().body("Friend request already exists");
        }
        
        // Check if friend request already exists in the other direction
        Optional<Friendship> reverseRequest = friendshipRepository.findByUserAndFriend(friend, Optional.ofNullable(currentUser));
        if (reverseRequest.isPresent()) {
            return ResponseEntity.badRequest().body("Friend request already exists in the other direction");
        }
        
        // Create new friendship
        Friendship friendship = Friendship.builder()
                .user(currentUser)
                .friend(friend)
                .accepted(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        friendshipRepository.save(friendship);
        
        return ResponseEntity.ok().build();
    }
    

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID friendshipId) {
        
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));
        
        // Check if the current user is the friend in this friendship
        if (!friendship.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body("You can only accept friend requests sent to you");
        }
        
        // Check if already accepted
        if (friendship.isAccepted()) {
            return ResponseEntity.badRequest().body("Friend request already accepted");
        }
        
        // Accept the friendship
        friendship.setAccepted(true);
        friendship.setAcceptedAt(LocalDateTime.now());
        
        friendshipRepository.save(friendship);
        
        return ResponseEntity.ok().build();
    }
    

    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<?> rejectOrRemoveFriend(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID friendshipId) {
        
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));
        
        // Check if the current user is part of this friendship
        if (!friendship.getUser().getId().equals(currentUser.getId()) && 
            !friendship.getFriend().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body("You are not part of this friendship");
        }
        
        // Delete the friendship
        friendshipRepository.delete(friendship);
        
        return ResponseEntity.ok().build();
    }
    

    @GetMapping("/search")
    public ResponseEntity<List<FriendDTO>> searchUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam String query) {
        
        User currentUser = jwtUtils.getUserFromAccessToken(token);
        
        // Find users whose username contains the query
        List<User> users = userRepository.findByFullName(query);
        
        // Remove current user from results
        users = users.stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .toList();
        
        // Convert to DTOs
        List<FriendDTO> results = users.stream().filter(Objects::nonNull)
                .map(user -> {
                    // Check if there's an existing friendship
                    Optional<Friendship> friendship = friendshipRepository.findByUserAndFriend(currentUser, Optional.of(user));
                    Optional<Friendship> reverseFriendship = friendshipRepository.findByUserAndFriend(user, Optional.of(currentUser));
                    
                    boolean isPending = false;
                    boolean isAccepted = false;
                    boolean isIncoming = false;
                    
                    if (friendship.isPresent()) {
                        isPending = !friendship.get().isAccepted();
                        isAccepted = friendship.get().isAccepted();
                    } else if (reverseFriendship.isPresent()) {
                        isPending = !reverseFriendship.get().isAccepted();
                        isAccepted = reverseFriendship.get().isAccepted();
                        isIncoming = true;
                    }
                    
                    return FriendDTO.builder()
                            .userId(user.getId())
                            .fullName(user.getFullName())
                            .xpPoints(user.getUserProfile().getXpPoints())
                            .streak(user.getUserProfile().getStreak())
                            .isPending(isPending)
                            .accepted(isAccepted)
                            .isIncoming(isIncoming)
                            .build();
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(results);
    }

    private FriendDTO mapToFriendDTO(Friendship friendship, User currentUser) {
        // Determine which user is the friend (not the current user)
        User friend = friendship.getUser().getId().equals(currentUser.getId()) 
                ? friendship.getFriend() 
                : friendship.getUser();
        
        boolean isIncoming = friendship.getFriend().getId().equals(currentUser.getId());
        
        return FriendDTO.builder()
                .id(friendship.getId())
                .userId(friend.getId())
                .fullName(friend.getFullName())
                .xpPoints(friend.getUserProfile().getXpPoints())
                .streak(friend.getUserProfile().getStreak())
                .accepted(friendship.isAccepted())
                .isPending(!friendship.isAccepted())
                .isIncoming(isIncoming)
                .createdAt(friendship.getCreatedAt())
                .acceptedAt(friendship.getAcceptedAt())
                .build();
    }
}