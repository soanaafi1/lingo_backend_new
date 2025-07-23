package com.backend.pandylingo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("translation")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TranslationExercise extends Exercise {

    public TranslationExercise() {}

    @Override
    public boolean validateAnswer(String answer) {
        // Validation uses the inherited 'correctAnswer' field
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer.trim());
    }
}
