package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends HttpException {
    public AccountLockedException() {
        super(HttpStatus.FORBIDDEN, "Account is locked");
    }
}
