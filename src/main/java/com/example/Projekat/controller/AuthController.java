package com.example.Projekat.controller;

import com.example.Projekat.dto.auth.PasswordResetRequest;
import com.example.Projekat.dto.auth.PasswordResetResponse;
import com.example.Projekat.logging.LogUserAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.Projekat.model.User;
import com.example.Projekat.service.AuthService;
import com.example.Projekat.service.UserService;
import com.example.Projekat.dto.UserDto;
import com.example.Projekat.dto.auth.AuthRequest;
import com.example.Projekat.dto.auth.AuthResponse;
import com.example.Projekat.dto.mapper.UserDtoMapper;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final AuthService _authService;
    private final UserService _userService;
    private final UserDtoMapper _userDtoMapper;

    @PostMapping("/login")
    @LogUserAction("User login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody @Valid final AuthRequest authRequest)
    {
        final AuthResponse authResponse =
                _authService.generateAuthToken(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    @LogUserAction("User registered")
    public UserDto createUser(@RequestBody @Valid final UserDto userDto)
    {
        final User savedUser = _userService.create(_userDtoMapper.fromDto(userDto));
        return _userDtoMapper.toDto(savedUser);
    }

    @PutMapping("/users/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @LogUserAction("User Edited")
    public UserDto updateUser(
            @PathVariable final Long id,
            @RequestBody @Valid final UserDto userDetails)
    {
        final User updatedUser = _userService.update(id, _userDtoMapper.fromDto(userDetails),true);
        return _userDtoMapper.toDto(updatedUser);
    }

    @PostMapping("/reset-password")
    @LogUserAction("User Requested Password Reset")
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @RequestBody @Valid final PasswordResetRequest request) {
        String token = _authService.generatePasswordResetToken(request.getEmail());

        return ResponseEntity.ok(
                PasswordResetResponse.builder()
                        .message("Token za reset lozinke uspešno generisan.")
                        .resetToken(token)
                        .build()
        );
    }
}

