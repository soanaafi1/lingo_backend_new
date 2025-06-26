package com.backend.pandylingo.security;

import com.backend.pandylingo.exception.InvalidTokenException;
import com.backend.pandylingo.exception.UnauthenticatedException;
import com.backend.pandylingo.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private final UserDetailsServiceImpl userDetailsService;

    public String generateAccessToken(UserDetails userDetails) {
        User user = (User) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        return buildAccessToken(claims, user.getId().toString());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        User user = (User) userDetails;
        return buildRefreshToken(new HashMap<>(), user.getId().toString());
    }

    private String buildAccessToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getAccessSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public UUID getUserIdFromAccessToken(String token) {
        return UUID.fromString(extractClaimForAccess(token, Claims::getSubject));
    }

    public UUID getUserIdFromRefreshToken(String token) {
        return UUID.fromString(extractClaimForRefresh(token, Claims::getSubject));
    }

    public User getUserFromAccessToken(String token) {
        String jwt = token.substring(7);
        UUID userId = getUserIdFromAccessToken(jwt);

        Optional<User> user = userDetailsService.loadUserById(userId);

        if (user.isEmpty()) {
            throw new UnauthenticatedException("User not found");
        }

        return user.get();
    }

    public User getUserFromRefreshToken(String token) {
        UUID userId = getUserIdFromRefreshToken(token);

        Optional<User> user = userDetailsService.loadUserById(userId);

        if (user.isEmpty()) {
            throw new UnauthenticatedException("User not found");
        }

        return user.get();
    }

    public void validateAccessToken(String token) {
        validateToken(token, getAccessSigningKey());
    }

    public void validateRefreshToken(String token) {
        validateToken(token, getRefreshSigningKey());
    }

    private void validateToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Invalid token");
        } catch (JwtException e) {
            throw new InvalidTokenException("Token validation failed. Token type might be invalid");
        }
    }

    public <T> T extractClaimForAccess(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, getAccessSigningKey());
        return claimsResolver.apply(claims);
    }

    public <T> T extractClaimForRefresh(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, getRefreshSigningKey());
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getAccessSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(accessSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Key getRefreshSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(refreshSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}