package com.backend.pandylingo.dto.auth;

import com.backend.pandylingo.model.Difficulty;
import com.backend.pandylingo.model.Language;
import com.backend.pandylingo.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class RegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Map<Language, Difficulty> languageProficiencies;

    private int age;

    @NotBlank
    private Role role;
}