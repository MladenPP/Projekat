package com.example.Projekat.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthRequest
{
    @NotBlank(message = "Username cannot be blank")
    String username;

    @NotNull(message = "Password cannot be null")
    String password;
}
