package com.backend.pandylingo.service;

import com.backend.pandylingo.dto.auth.*;
import com.backend.pandylingo.exception.*;
import com.backend.pandylingo.model.Role;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.model.UserProfile;
import com.backend.pandylingo.repository.UserRepository;
import com.backend.pandylingo.security.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AppStatsService appStatsService;

    @Transactional
    public LoginResponse loginUser(LoginRequest request) {
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

            return buildLoginResponse(userDetails, accessToken, refreshToken);

        } catch (BadCredentialsException ex) {
            throw new UnauthenticatedException("Incorrect email or password");
        } catch (AuthenticationException ex) {
            throw new UnauthenticatedException(ex.getMessage());
        }
    }

    @Transactional
    public String registerUser(RegisterRequest request) {
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

            if (user.getRole() == Role.USER) {
                appStatsService.incrementUsersCount();
            }

            return "User registered successfully";

        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid registration data", ex.getMostSpecificCause().getMessage());
        } catch (DataAccessException ex) {
            throw new InternalServerErrorException("Failed to register user");
        }
    }

    @Transactional
    public TokenRefreshResponse getNewAccessToken(TokenRefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            jwtUtils.validateRefreshToken(refreshToken);

            User userDetails = jwtUtils.getUserFromRefreshToken(refreshToken);

            String newAccessToken = jwtUtils.generateAccessToken(userDetails);
            String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

            return TokenRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();

        } catch (ExpiredJwtException ex) {
            throw new UnauthenticatedException("Refresh token expired");
        } catch (JwtException ex) {
            throw new UnauthenticatedException("Invalid refresh token");
        }
    }


    // Helper methods
    private LoginResponse buildLoginResponse(User userDetails, String accessToken, String refreshToken) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .userId(userDetails.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authorities(authorities)
                .role(userDetails.getRole())
                .build();
    }

    private User buildNewUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());

        if (request.getRole() == Role.USER) {
            UserProfile userProfile = new UserProfile();
            userProfile.setAge(request.getAge());
            userProfile.setLanguageProficiencies(request.getLanguageProficiencies());
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
        }

        return  userRepository.save(user);
    }
}
