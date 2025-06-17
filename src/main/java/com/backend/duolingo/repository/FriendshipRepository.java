package com.backend.duolingo.repository;

import com.backend.duolingo.model.Friendship;
import com.backend.duolingo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    
    // Find all accepted friendships where the user is either the user or the friend
    @Query("SELECT f FROM Friendship f WHERE (f.user = ?1 OR f.friend = ?1) AND f.accepted = true")
    List<Friendship> findAllAcceptedByUser(User user);
    
    // Find all pending friendships where the user is the friend (friend requests received)
    @Query("SELECT f FROM Friendship f WHERE f.friend = ?1 AND f.accepted = false")
    List<Friendship> findAllPendingByFriend(User user);
    
    // Find all pending friendships where the user is the user (friend requests sent)
    @Query("SELECT f FROM Friendship f WHERE f.user = ?1 AND f.accepted = false")
    List<Friendship> findAllPendingByUser(User user);
    
    // Find a friendship between two users
    Optional<Friendship> findByUserAndFriend(User user, Optional<User> friend);
}