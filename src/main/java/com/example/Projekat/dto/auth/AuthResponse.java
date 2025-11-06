package com.example.Projekat.dto.auth;

import lombok.Builder;
import lombok.Value;
import com.example.Projekat.dto.UserDto;

@Value
@Builder
public class AuthResponse
{
    String token;
    UserDto user;
}
