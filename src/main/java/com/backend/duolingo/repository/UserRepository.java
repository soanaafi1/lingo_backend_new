package com.backend.duolingo.repository;

import com.backend.duolingo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
//  Optional<User> findByEmail(String email); Could be used for email-based login and password changing
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<User> findByHeartsLessThan(int i);

    // Find users by username containing a string (case insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> findByUsernameContainingIgnoreCase(String username);
}
