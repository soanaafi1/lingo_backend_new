package com.backend.pandylingo.exception;

import org.springframework.http.HttpStatus;

// 404 Not Found
public class NotFoundException extends HttpException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(Class<?> entityClass, Object identifier) {
        super(HttpStatus.NOT_FOUND,
                "%s with id %s not found".formatted(entityClass.getSimpleName(), identifier));
    }
}
