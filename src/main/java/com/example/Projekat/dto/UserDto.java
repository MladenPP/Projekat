package com.example.Projekat.dto;

import lombok.Builder;
import lombok.Value;
import com.example.Projekat.model.Role;

import java.util.EnumSet;
import java.util.Set;

@Value
@Builder
public class UserDto
{
    Long id;

    String username;
    String password;

    String firstName;
    String lastName;
    String email;
    String phone;

    boolean enabled;
    boolean expired;
    boolean locked;
    boolean credentialsExpired;
    boolean shouldChangePassword;

    @Builder.Default
    Set<Role> roles = EnumSet.of(Role.GUEST);
}
