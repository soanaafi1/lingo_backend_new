package com.backend.pandylingo.repository;

import com.backend.pandylingo.model.ApplicationStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppStatsRepository
        extends JpaRepository<ApplicationStats, String> {
}
