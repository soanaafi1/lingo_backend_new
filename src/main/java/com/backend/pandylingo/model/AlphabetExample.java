package com.backend.pandylingo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alphabet_examples")
public class AlphabetExample {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "alphabet_id", nullable = false)
    private Alphabets alphabet;

    private String word;
    private String meaning;
    private String audioUrl;
}