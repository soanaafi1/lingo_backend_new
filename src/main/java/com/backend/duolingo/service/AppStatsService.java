package com.backend.duolingo.service;

import com.backend.duolingo.model.ApplicationStats;
import com.backend.duolingo.repository.AppStatsRepository;
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

    public void incrementCoursesCount() {
        updateStat("totalCourses", 1L);
    }
    public void incrementLessonsCount() {
        updateStat("totalLessons", 1L);
    }
    public void incrementExercisesCount() {
        updateStat("totalExercises", 1L);
    }
    public void incrementUsersCount() {
        updateStat("totalUsers", 1L);
    }

    public void decrementCourses() {
        updateStat("totalCourses",-1L);
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

    @Transactional
    @PostConstruct
    public void init() {
        if (!statsRepo.existsById("GLOBAL_STATS")) {
            ApplicationStats stats = new ApplicationStats();
            statsRepo.save(stats);
        }
    }
}
