package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends HttpException {
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid token");
    }
    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}