package com.backend.duolingo.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("TRANSLATION")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TranslationExercise extends Exercise {
    @Column(nullable = false)
    private String correctAnswer;

    public TranslationExercise() {

    }

    @Override
    public boolean validateAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }
}