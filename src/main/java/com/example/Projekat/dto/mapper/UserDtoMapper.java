package com.example.Projekat.dto.mapper;

import org.springframework.stereotype.Component;
import com.example.Projekat.model.User;
import com.example.Projekat.dto.UserDto;

@Component
public class UserDtoMapper
{
    public UserDto toDto(final User user)
    {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                // password is not mapped, intentionally
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .locked(user.isLocked())
                .expired(user.isExpired())
                .credentialsExpired(user.isCredentialsExpired())
                .enabled(user.isEnabled())
                .shouldChangePassword(user.isShouldChangePassword())
                .roles(user.getRoles())
                .build();
    }

    public User fromDto(final UserDto userDto)
    {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .locked(userDto.isLocked())
                .expired(userDto.isExpired())
                .credentialsExpired(userDto.isCredentialsExpired())
                .enabled(userDto.isEnabled())
                .shouldChangePassword(userDto.isShouldChangePassword())
                .roles(userDto.getRoles())
                .build();
    }
}

