package com.backend.pandylingo.exception;

import org.springframework.http.HttpStatus;

// 500 Internal Server Error
public class InternalServerErrorException extends HttpException {
    public InternalServerErrorException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
