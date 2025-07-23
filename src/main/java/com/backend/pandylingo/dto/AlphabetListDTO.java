package com.backend.pandylingo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlphabetListDTO {
    private String id;
    private String character;
    private String pronunciation;
    private String difficulty;
}
