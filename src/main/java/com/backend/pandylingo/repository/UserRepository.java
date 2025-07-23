package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> findByFullName(String fullName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile LEFT JOIN FETCH u.userProfile.languageProficiencies WHERE  u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE  u.id = :id")
    Optional<User> findByIdWithoutProfile(@Param("id") UUID id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE  u.id = :id AND u.userProfile.hearts < :i")
    Optional<User> findByHeartsWithProfile(@Param("id") UUID id, @Param("hearts") int i);

}
