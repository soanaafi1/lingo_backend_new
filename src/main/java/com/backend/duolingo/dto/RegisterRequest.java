package com.backend.duolingo.dto;

import com.backend.duolingo.model.Difficulty;
import com.backend.duolingo.model.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private Map<Language, Difficulty> language;

    @NotBlank
    private int age;
}