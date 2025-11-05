package com.security.api.service;

import com.security.api.dto.*;
import com.security.api.entity.User;
import com.security.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoles(Set.of("ROLE_USER"));
        userRepository.save(user);

        return new AuthResponse(null, "User registered successfully", user.getUsername());
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Bad credentials");
        }

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[0]))
                        .build()
        );

        return new AuthResponse(token, "Authentication successful", user.getUsername());
    }
}
