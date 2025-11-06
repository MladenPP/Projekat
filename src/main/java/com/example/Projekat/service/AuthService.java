package com.example.Projekat.service;

import com.example.Projekat.db.entity.UserEntity;
import com.example.Projekat.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.Projekat.config.security.CustomUserDetails;
import com.example.Projekat.config.security.jwt.JwtTokenUtil;
import com.example.Projekat.model.Role;
import com.example.Projekat.dto.UserDto;
import com.example.Projekat.dto.auth.AuthResponse;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final AuthenticationManager _authenticationManager;
    private final JwtTokenUtil _jwtTokenUtil;
    private final UserRepository _userRepository;

    public AuthResponse generateAuthToken(final String username, final String password)
    {
        final Authentication authentication = _authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        if (!authentication.isAuthenticated())
        {
            throw new BadCredentialsException("Invalid username and password");
        }

        if (authentication.getPrincipal() instanceof final CustomUserDetails userDetails)
        {
            final String token = _jwtTokenUtil.generateToken(null, userDetails);

            final UserDto userDto = UserDto.builder()
                    .username(userDetails.getUsername())
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .locked(!userDetails.isAccountNonLocked())
                    .expired(!userDetails.isAccountNonExpired())
                    .credentialsExpired(!userDetails.isCredentialsNonExpired())
                    .enabled(userDetails.isEnabled())
                    .shouldChangePassword(userDetails.shouldChangePassword())
                    .roles(userDetails.getRoles().stream()
                            .map(Role::fromString)
                            .collect(Collectors.toSet()))
                    .build();

            return AuthResponse.builder()
                    .token(token)
                    .user(userDto)
                    .build();
        }

        throw new InternalAuthenticationServiceException("Unknown error during authentication");
    }

    public String generatePasswordResetToken(final String email) {
        UserEntity user = _userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ovim e-mailom ne postoji."));

        return _jwtTokenUtil.generatePasswordResetToken(user);
    }


}

