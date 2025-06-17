package com.backend.duolingo.controller;

import com.backend.duolingo.dto.LoginRequest;
import com.backend.duolingo.dto.LoginResponse;
import com.backend.duolingo.dto.RegisterRequest;
import com.backend.duolingo.dto.UpdateAvatarRequest;
import com.backend.duolingo.model.Difficulty;
import com.backend.duolingo.model.Language;
import com.backend.duolingo.model.Role;
import com.backend.duolingo.model.User;
import com.backend.duolingo.repository.UserRepository;
import com.backend.duolingo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(user);

        // Extract authorities as strings
        List<String> authoritiesAsStrings = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(LoginResponse.builder()
                .token(jwt)
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .xpPoints(user.getXpPoints())
                .streak(user.getStreak())
                .hearts(user.getHearts())
                .role(user.getRole())
                .authorities(authoritiesAsStrings)
                .avatarUrl(user.getAvatarUrl())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .xpPoints(0)
                .streak(0)
                .lastStreakUpdate(now)
                .streakFreezeCount(0)
                .hearts(5)
                .lastHeartRefill(now)
                .role(Role.USER)
                .avatarUrl("https://ui-avatars.com/api/?name=" + request.getFullName())
                .languages(request.getLanguage() != null ? request.getLanguage() : new HashMap<>())
                .age(request.getAge())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<LoginResponse> updateAvatar(@AuthenticationPrincipal User user, @RequestBody UpdateAvatarRequest request) {
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);

        // Extract authorities as strings
        List<String> authoritiesAsStrings = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(LoginResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .xpPoints(user.getXpPoints())
                .streak(user.getStreak())
                .hearts(user.getHearts())
                .role(user.getRole())
                .authorities(authoritiesAsStrings)
                .avatarUrl(user.getAvatarUrl())
                .build());
    }
}
