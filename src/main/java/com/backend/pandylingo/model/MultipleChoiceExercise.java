package com.backend.pandylingo.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("multiple_choice")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MultipleChoiceExercise extends Exercise {

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "multiple_choice_options", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "option")
    private List<String> options = new ArrayList<>();

    public MultipleChoiceExercise() {}

    @Override
    public boolean validateAnswer(String answer) {
        // Validation uses the inherited 'correctAnswer' field
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer.trim());
    }
}
