package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends HttpException {
    public UnauthenticatedException() {
        super(HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    public UnauthenticatedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}

