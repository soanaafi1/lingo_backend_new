package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    List<UserProfile> findByHeartsLessThan(int i);

    @Query("SELECT u.languageProficiencies FROM UserProfile u WHERE  u.user.id = :id")
    List<Map<Language, Difficulty>> findUserLanguagesByUserId(@Param("id") UUID id);
}
