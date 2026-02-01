package com.sky.movieratingservice.api.controller.auth;

import com.sky.movieratingservice.api.dto.AuthLoginRequest;
import com.sky.movieratingservice.api.dto.AuthRegisterRequest;
import com.sky.movieratingservice.api.dto.TokenResponse;
import com.sky.movieratingservice.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody AuthRegisterRequest request) {
        authService.register(request.email(), request.password());
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request.email(), request.password());
    }
}
