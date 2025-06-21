package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

// 403 Forbidden
public class ForbiddenException extends HttpException {
    public ForbiddenException(String message, String exMessage) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
