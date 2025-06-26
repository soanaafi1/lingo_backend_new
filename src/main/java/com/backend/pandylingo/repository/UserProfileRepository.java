package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    List<UserProfile> findByHeartsLessThan(int i);
}
