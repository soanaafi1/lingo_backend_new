package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

// 409 Conflict
public class ConflictException extends HttpException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
