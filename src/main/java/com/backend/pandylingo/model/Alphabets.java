package com.backend.pandylingo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alphabets")
public class Alphabets {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private Difficulty difficulty;
    private char character;
    private String name;
    private String pronunciation;
    private String ipa;
    private String mouthPosition;
    private String tonguePosition;
    private String commonMispronunciations;
    private String audioUrl;
    private String slowAudioUrl;
    private String nativeAudioUrl;

    @OneToMany(mappedBy = "alphabet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlphabetExample> examples;

    private String similarTo;
}
