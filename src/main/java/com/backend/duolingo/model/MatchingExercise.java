package com.backend.duolingo.model;

import jakarta.persistence.*;
import jakarta.persistence.DiscriminatorValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("matching")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MatchingExercise extends Exercise {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "matching_exercise_pairs", joinColumns = @JoinColumn(name = "exercise_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> pairs = new HashMap<>();

    @Column(name = "correct_index", nullable = false)
    private int correctOptionIndex = 0; // Default value to satisfy not-null constraint

    public MatchingExercise() {

    }

    @Override
    public boolean validateAnswer(String answer) {
        // Format: "key1:value1,key2:value2"
        Map<String, String> answerMap = parseAnswer(answer);
        return pairs.equals(answerMap);
    }

    private Map<String, String> parseAnswer(String answer) {
        return Arrays.stream(answer.split(","))
                .map(pair -> pair.split(":"))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }
}
