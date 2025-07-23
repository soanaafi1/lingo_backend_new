package com.backend.pandylingo.model;

import lombok.Getter;

@Getter
public enum Language {
    JAPANESE("ja"),
    SPANISH("es"),
    FRENCH("fr"),
    GERMAN("de");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public static Language fromCode(String code) {
        for (Language language : values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Invalid language code: " + code);
    }
}