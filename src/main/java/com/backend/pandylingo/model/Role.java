package com.backend.pandylingo.model;


public enum Role implements org.springframework.security.core.GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}