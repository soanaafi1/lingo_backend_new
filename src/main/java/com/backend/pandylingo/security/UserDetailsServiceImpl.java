package com.backend.pandylingo.security;

import com.backend.pandylingo.exception.NotFoundException;
import com.backend.pandylingo.model.User;
import com.backend.pandylingo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws NotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Optional<User> loadUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> loadUserByIdWithProfile(UUID userId) {
        return userRepository.findById(userId);
    }
}