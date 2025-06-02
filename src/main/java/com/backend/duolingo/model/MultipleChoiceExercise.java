package com.backend.duolingo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MultipleChoiceExercise extends Exercise {
    @ElementCollection
    @CollectionTable(name = "exercise_options", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "option")
    private List<String> options = new ArrayList<>();

    @Column(name = "correct_index", nullable = false)
    private int correctOptionIndex;

    public MultipleChoiceExercise() {

    }

    @Override
    public boolean validateAnswer(String answer) {
        try {
            int selectedIndex = Integer.parseInt(answer);
            return selectedIndex == correctOptionIndex;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}