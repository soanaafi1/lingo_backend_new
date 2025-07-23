package com.backend.pandylingo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private int age;

    private String avatarUrl;

    private int xpPoints;

    private int streak;

    @Column(name = "last_streak_update")
    private LocalDateTime lastStreakUpdate;

    @Column(name = "streak_freeze_count")
    private int streakFreezeCount;

    private int hearts;

    @Column(name = "last_heart_refill")
    private LocalDateTime lastHeartRefill;

    @ElementCollection
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "language")
    @Column(name = "difficulty")
    @MapKeyEnumerated(EnumType.STRING)
    @Enumerated(EnumType.STRING)
    private Map<Language, Difficulty> languageProficiencies;

    @PrePersist
    protected void onCreate() {
        // Initialize hearts
        if (hearts <= 0) {
            hearts = 5;
        }

        // Set initial heart refill time
        if (lastHeartRefill == null) {
            lastHeartRefill = LocalDateTime.now();
        }

        // Initialize streak fields
        if (lastStreakUpdate == null) {
            lastStreakUpdate = LocalDateTime.now();
        }

        // Initialize streak freeze count
        if (streakFreezeCount < 0) {
            streakFreezeCount = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        // Refill hearts if it's been more than 5 hours since last refill
        if (lastHeartRefill == null ||
                lastHeartRefill.isBefore(LocalDateTime.now().minusHours(5))) {

            if (hearts < 5) {
                hearts = 5;
                lastHeartRefill = LocalDateTime.now();
            }
        }
    }

}
