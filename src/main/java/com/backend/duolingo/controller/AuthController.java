package com.backend.duolingo.controller;

import com.backend.duolingo.dto.auth.*;
import com.backend.duolingo.exception.*;
import com.backend.duolingo.model.*;
import com.backend.duolingo.repository.UserRepository;
import com.backend.duolingo.security.JwtUtils;
import com.backend.duolingo.security.UserDetailsServiceImpl;
import com.backend.duolingo.service.AppStatsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AppStatsService appStatsService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User userDetails = (User) authentication.getPrincipal();

            String accessToken = jwtUtils.generateAccessToken(userDetails);
            String refreshToken = jwtUtils.generateRefreshToken(userDetails);

            return ResponseEntity.ok(buildLoginResponse(userDetails, accessToken, refreshToken));

        } catch (BadCredentialsException ex) {
            throw new UnauthenticatedException("Invalid email or password");
        } catch (DisabledException ex) {
            throw new ForbiddenException("Account disabled", ex.getMessage());
        } catch (LockedException ex) {
            throw new ForbiddenException("Account locked", ex.getMessage());
        } catch (AuthenticationException ex) {
            throw new UnauthenticatedException(ex.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            // Validate request
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new BadRequestException("Email is required");
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new BadRequestException("Password is required");
            }
            if (request.getPassword().length() < 8) {
                throw new BadRequestException("Password must be at least 8 characters");
            }
            if (request.getFullName() == null || request.getFullName().isBlank()) {
                throw new BadRequestException("Full name is required");
            }

            // Check for existing user
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already in use");
            }

            // Create new user
            User user = buildNewUser(request);
            userRepository.save(user);
            appStatsService.incrementUsersCount();

            return ResponseEntity.ok("User registered successfully");

        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid registration data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to register user", ex.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtUtils.validateRefreshToken(refreshToken)) {
                throw new UnauthenticatedException("Invalid refresh token");
            }

            UUID userId = jwtUtils.getUserIdFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserById(userId);

            String newAccessToken = jwtUtils.generateAccessToken(userDetails);
            String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

            return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken));

        } catch (ExpiredJwtException ex) {
            throw new UnauthenticatedException("Refresh token expired");
        } catch (JwtException ex) {
            throw new UnauthenticatedException("Invalid refresh token");
        }
    }

    private User buildNewUser(RegisterRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
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
    }

    private LoginResponse buildLoginResponse(User userDetails, String accessToken, String refreshToken) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .id(userDetails.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authorities(authorities)
                .role(userDetails.getRole())
                .build();
    }
}