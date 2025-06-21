package com.backend.duolingo.repository;

import com.backend.duolingo.model.ApplicationStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppStatsRepository
        extends JpaRepository<ApplicationStats, String> {
}
