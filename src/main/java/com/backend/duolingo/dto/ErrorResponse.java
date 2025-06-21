package com.backend.duolingo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        @JsonProperty("status_code") int statusCode,
        String message,
        Object detail,
        @JsonProperty("error") String error
) {
    public ErrorResponse(HttpStatus status, String message, Object detail) {
        this(status.value(), message, detail, status.getReasonPhrase());
    }
}