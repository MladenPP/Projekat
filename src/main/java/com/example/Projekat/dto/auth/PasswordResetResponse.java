package com.example.Projekat.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetResponse {
    private String message;
    private String resetToken;
}