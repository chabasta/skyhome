package com.sky.movieratingservice.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        @Size(min = 8, max = 100)
        String password
) {
}
