package com.example.Projekat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class User
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

    Set<Role> roles;
}

