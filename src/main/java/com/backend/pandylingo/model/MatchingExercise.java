package com.backend.pandylingo.model;

import jakarta.persistence.*;
import lombok.Builder;
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

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "matching_exercise_pairs", joinColumns = @JoinColumn(name = "exercise_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> pairs = new HashMap<>();

    public MatchingExercise() {}

    @Override
    public boolean validateAnswer(String answer) {
        Map<String, String> answerMap = parseAnswer(answer);
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer.trim());
    }

    private Map<String, String> parseAnswer(String answer) {
        // Utility to parse the incoming answer string (e.g., "key1:value1,key2:value2") into a map
        return Arrays.stream(answer.split(","))
                .map(pair -> pair.split(":"))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }
}
