package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

// 401 Unauthorized
public class UnauthorizedException extends HttpException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
