package com.backend.duolingo.scheduler;

import com.backend.duolingo.model.User;
import com.backend.duolingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HeartRefillScheduler {
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 30 * 60 * 1000) // Run every 30 minutes
    @Transactional
    public void refillHearts() {
        List<User> users = userRepository.findByHeartsLessThan(5);

        for (User user : users) {
            if (user.getLastHeartRefill() == null ||
                    user.getLastHeartRefill().isBefore(LocalDateTime.now().minusHours(5))) {

                user.setHearts(Math.min(5, user.getHearts() + 1));
                user.setLastHeartRefill(LocalDateTime.now());
            }
        }

        userRepository.saveAll(users);
    }
}