package com.backend.duolingo.repository;

import com.backend.duolingo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByHeartsLessThan(int i);

    // Find users by full name containing a string (case-insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> findByFullName(String fullName);
}
