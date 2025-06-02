package com.backend.duolingo.scheduler;

import com.backend.duolingo.model.User;
import com.backend.duolingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StreakScheduler {
    private final UserRepository userRepository;

    /**
     * Updates user streaks daily at midnight.
     * - If a user has practiced today (lastStreakUpdate is today), their streak continues
     * - If a user hasn't practiced today but has a streak freeze, use the freeze and maintain streak
     * - If a user hasn't practiced and has no streak freeze, reset streak to 0
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void updateStreaks() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<User> allUsers = userRepository.findAll();
        
        for (User user : allUsers) {
            LocalDate lastUpdate = user.getLastStreakUpdate().toLocalDate();
            
            // User practiced today, nothing to do
            if (lastUpdate.equals(today)) {
                continue;
            }
            
            // User practiced yesterday, streak is still valid
            if (lastUpdate.equals(yesterday)) {
                continue;
            }
            
            // User didn't practice yesterday, check for streak freeze
            if (user.getStreakFreezeCount() > 0) {
                // Use streak freeze
                user.setStreakFreezeCount(user.getStreakFreezeCount() - 1);
            } else {
                // Reset streak
                user.setStreak(0);
            }
        }
        
        userRepository.saveAll(allUsers);
    }
}