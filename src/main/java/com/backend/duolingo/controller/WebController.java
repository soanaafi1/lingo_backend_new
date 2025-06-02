package com.backend.duolingo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving web pages
 */
@Controller
public class WebController {

    /**
     * Redirect root URL to the admin login page
     * @return redirect to login.html
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }

    /**
     * Endpoint to validate admin token
     * This is called from the frontend to check if the token is valid
     * @return 200 OK if token is valid (Spring Security will handle the authentication)
     */
    @GetMapping("/api/admin/validate")
    public ResponseEntity<String> validateToken() {
        // If the request reaches here, it means the token is valid
        // (Spring Security would have blocked it otherwise)
        return ResponseEntity.ok("OK");
    }
}
