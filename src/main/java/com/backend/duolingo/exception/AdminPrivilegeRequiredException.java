package com.backend.duolingo.exception;

import org.springframework.http.HttpStatus;

public class AdminPrivilegeRequiredException extends HttpException {
    public AdminPrivilegeRequiredException() {
        super(HttpStatus.FORBIDDEN, "Admin privileges required");
    }

    public AdminPrivilegeRequiredException(String operation) {
        super(HttpStatus.FORBIDDEN,
                "Admin privileges required for: " + operation);
    }
}
