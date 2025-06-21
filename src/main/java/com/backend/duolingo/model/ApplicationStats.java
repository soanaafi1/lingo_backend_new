package com.backend.duolingo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "app_stats")
public class ApplicationStats {

    @Id
    private String id = "GLOBAL_STATS";

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long totalCourses = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long totalLessons = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long totalExercises = 0L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long totalUsers = 0L;
}
