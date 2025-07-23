package com.backend.pandylingo.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AlphabetDetailDTO {
    private String id;
    private String character;
    private String name;
    private String pronunciation;
    private String ipa;
    private String mouthPosition;
    private String tonguePosition;
    private List<String> commonMispronunciations;
    private String audioUrl;
    private String audioUrlSlow;
    private String audioUrlNative;
    private List<ExampleDTO> examples;
    private String similarTo;
    private String difficulty;
}

@Data
@Builder
class ExampleDTO {
    private String word;
    private String meaning;
    private String audioUrl;
}