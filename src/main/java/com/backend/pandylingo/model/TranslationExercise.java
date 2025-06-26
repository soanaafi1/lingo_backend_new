package com.backend.pandylingo.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("translation")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TranslationExercise extends Exercise {

    @Builder.Default
    @Column(name = "correct_index", nullable = false)
    private int correctOptionIndex = 0; // Default value to satisfy not-null constraint

    public TranslationExercise() {}

    @Override
    public boolean validateAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }
}
