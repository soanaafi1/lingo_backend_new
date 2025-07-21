package com.backend.pandylingo.scheduler;

import com.backend.pandylingo.model.UserProfile;
import com.backend.pandylingo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HeartRefillScheduler {
    private final UserProfileRepository userProfileRepository;

    @Scheduled(fixedRate = 30 * 60 * 1000) // Run every 30 minutes
    @Transactional
    public void refillHearts() {
        List<UserProfile> users = userProfileRepository.findByHeartsLessThan(5);

        for (UserProfile user: users) {
            if (user.getLastHeartRefill() == null ||
                    user.getLastHeartRefill().isBefore(LocalDateTime.now().minusHours(5))) {

                user.setHearts(Math.min(5, user.getHearts() + 1));
                user.setLastHeartRefill(LocalDateTime.now());
            }
        }

        userProfileRepository.saveAll(users);
    }
}