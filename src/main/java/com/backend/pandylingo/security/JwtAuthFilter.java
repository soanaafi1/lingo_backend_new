package com.backend.pandylingo.security;

import com.backend.pandylingo.exception.InvalidTokenException;
import com.backend.pandylingo.exception.UnauthenticatedException;
import com.backend.pandylingo.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return new AntPathMatcher().match("/api/auth/**", request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthenticatedException("Missing or invalid Authorization header");
            }

            final String jwt = authHeader.substring(7);

            jwtUtils.validateAccessToken(jwt);

            final UUID userId = jwtUtils.getUserIdFromAccessToken(jwt);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> userDetails = this.userDetailsService.loadUserById(userId);

                if (userDetails.isEmpty()) {
                    throw new UnauthenticatedException("User not found");
                }

                User user = userDetails.get();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            handleException(response, new UnauthenticatedException("Access token expired"));
        } catch (JwtException ex) {
            handleException(response, new InvalidTokenException("Invalid token"));
        } catch (UnauthenticatedException | InvalidTokenException ex) {
            handleException(response, ex);
        }
    }

    private void handleException(HttpServletResponse response, RuntimeException ex) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(String.format("{\"message\":\"%s\"}", ex.getMessage()));
    }
}