package com.backend.pandylingo.service;

import com.backend.pandylingo.dto.stats.AppStatsResponse;
import com.backend.pandylingo.exception.InternalServerErrorException;
import com.backend.pandylingo.exception.NotFoundException;
import com.backend.pandylingo.model.ApplicationStats;
import com.backend.pandylingo.repository.AppStatsRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppStatsService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AppStatsRepository statsRepo;

    public void incrementLessonsCount() {
        updateStat("totalLessons", 1L);
    }
    public void incrementExercisesCount() {
        updateStat("totalExercises", 1L);
    }
    public void incrementUsersCount() {
        updateStat("totalUsers", 1L);
    }


    public void decrementLessons() {
        updateStat("totalLessons",-1L);
    }

    public void decrementExercises() {
        updateStat("totalExercises", -1L);
    }

    private void updateStat(String fieldName, Long delta) {
        String jpql = String.format("UPDATE ApplicationStats s SET s.%s = s.%s + :delta", fieldName, fieldName);

        entityManager.createQuery(jpql).setParameter("delta", delta).executeUpdate();
    }

    public AppStatsService(AppStatsRepository statsRepo) {
        this.statsRepo = statsRepo;
    }

    public AppStatsResponse getStats() {
        try{
            ApplicationStats stats = entityManager.find(ApplicationStats.class, "GLOBAL_STATS");

            if (stats == null) {
                throw new NotFoundException("Stats not found");
            }

            return AppStatsResponse.builder()
                    .totalExercises(stats.getTotalExercises())
                    .totalLessons(stats.getTotalLessons())
                    .totalLearners(stats.getTotalUsers())
                    .build();

        } catch (IllegalArgumentException e) {
            throw new InternalServerErrorException("Failed to retrieve app stats");
        }
    }

    @Transactional
    @PostConstruct
    public void init() {
        if (!statsRepo.existsById("GLOBAL_STATS")) {
            ApplicationStats stats = new ApplicationStats();
            statsRepo.save(stats);
        }
    }
}
