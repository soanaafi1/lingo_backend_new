package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

// 400 Bad Request
public class BadRequestException extends HttpException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, Object detail) {
        super(HttpStatus.BAD_REQUEST, message, detail);
    }
}

