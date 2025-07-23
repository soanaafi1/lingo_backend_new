package com.backend.pandylingo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "exercise_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "exercises")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TranslationExercise.class, name = "translation"),
        @JsonSubTypes.Type(value = MultipleChoiceExercise.class, name = "multiple_choice"),
        @JsonSubTypes.Type(value = MatchingExercise.class, name = "matching")
})
public abstract class Exercise {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Column(nullable = false)
    private String question;

    private String hint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnoreProperties("exercises")
    private Lesson lesson;

    private int xpReward;
    private int heartsCost;

    @Column(name = "correct_answer", nullable = false)
    protected String correctAnswer;

    public abstract boolean validateAnswer(String answer);
}
