package com.backend.pandylingo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_progress")
public class UserProgress {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private boolean completed;
    private boolean correct;
    private LocalDateTime completedAt;

    @Column(length = 1000)
    private String userAnswer;

    private int xpEarned;
    private int heartsUsed;
}
