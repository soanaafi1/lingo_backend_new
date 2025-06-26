package com.backend.pandylingo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpException extends RuntimeException {
    private final HttpStatus status;
    private final Object detail;

    public HttpException(HttpStatus status, String message) {
        this(status, message, null);
    }

    public HttpException(HttpStatus status, String message, Object detail) {
        super(message);
        this.status = status;
        this.detail = detail;
    }
}