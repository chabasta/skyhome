package com.sky.movieratingservice.service.auth;

import com.sky.movieratingservice.api.dto.TokenResponse;
import com.sky.movieratingservice.entity.User;
import com.sky.movieratingservice.repository.users.UserRepository;
import com.sky.movieratingservice.security.JwtProperties;
import com.sky.movieratingservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public void register(String email, String password) {
        String normalized = email.toLowerCase(Locale.ROOT).trim();
        if (userRepository.existsByEmail(normalized)) {
            throw new ResponseStatusException(BAD_REQUEST, "Email already registered");
        }
        User user = new User(
                UUID.randomUUID(),
                normalized,
                passwordEncoder.encode(password),
                Instant.now()
        );
        userRepository.save(user);
    }

    public TokenResponse login(String email, String password) {
        String normalized = email.toLowerCase(Locale.ROOT).trim();
        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.issueToken(user);
        return new TokenResponse(token, "Bearer", jwtProperties.ttl().toSeconds());
    }
}
