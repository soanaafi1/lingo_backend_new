package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

// 500 Internal Server Error
public class InternalServerErrorException extends HttpException {
    public InternalServerErrorException(String message, String exMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
