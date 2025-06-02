package com.backend.duolingo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private int xpPoints;
    private int streak;

    @Column(name = "last_streak_update")
    private LocalDateTime lastStreakUpdate;

    @Column(name = "streak_freeze_count")
    private int streakFreezeCount;

    private int hearts;
    @Column(name = "last_heart_refill")
    private LocalDateTime lastHeartRefill;

    @PrePersist
    protected void onCreate() {
        // Generate UUID if not provided
        if (id == null) {
            id = UUID.randomUUID();
        }

        // Initialize hearts if needed
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

    @Enumerated(EnumType.STRING)
    private Role role;

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
