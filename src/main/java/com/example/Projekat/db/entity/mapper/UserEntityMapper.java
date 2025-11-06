package com.example.Projekat.db.entity.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.Projekat.db.entity.UserEntity;
import com.example.Projekat.model.User;

@Component
@RequiredArgsConstructor
public class UserEntityMapper
{
    public UserEntity toEntity(final User user)
    {
        return UserEntity.builder()
                .id(user.getId())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .expired(user.isExpired())
                .locked(user.isLocked())
                .credentialsExpired(user.isCredentialsExpired())
                .shouldChangePassword(user.isShouldChangePassword())
                .roles(user.getRoles())
                .build();
    }

    public User fromEntity(final UserEntity userEntity)
    {
        return User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .enabled(userEntity.isEnabled())
                .expired(userEntity.isExpired())
                .locked(userEntity.isLocked())
                .credentialsExpired(userEntity.isCredentialsExpired())
                .shouldChangePassword(userEntity.isShouldChangePassword())
                .roles(userEntity.getRoles())
                .build();
    }
}

