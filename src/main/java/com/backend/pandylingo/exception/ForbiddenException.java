package com.backend.pandylingo.exception;

import org.springframework.http.HttpStatus;

// 403 Forbidden
public class ForbiddenException extends HttpException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
