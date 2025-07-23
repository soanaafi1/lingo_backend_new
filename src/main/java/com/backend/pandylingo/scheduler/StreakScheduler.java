package com.backend.pandylingo.scheduler;

import com.backend.pandylingo.model.UserProfile;
import com.backend.pandylingo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StreakScheduler {

    private final UserProfileRepository userProfileRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateStreaks() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<UserProfile> allUsers = userProfileRepository.findAll();
        
        for (UserProfile userProfile : allUsers) {
            LocalDate lastUpdate = userProfile.getLastStreakUpdate().toLocalDate();
            
            // User practiced today, nothing to do
            if (lastUpdate.equals(today)) {
                continue;
            }
            
            // User practiced yesterday, streak is still valid
            if (lastUpdate.equals(yesterday)) {
                continue;
            }
            
            // User didn't practice yesterday, check for streak freeze
            if (userProfile.getStreakFreezeCount() > 0) {
                // Use streak freeze
                userProfile.setStreakFreezeCount(userProfile.getStreakFreezeCount() - 1);
            } else {
                // Reset streak
                userProfile.setStreak(0);
            }
        }
        
        userProfileRepository.saveAll(allUsers);
    }
}