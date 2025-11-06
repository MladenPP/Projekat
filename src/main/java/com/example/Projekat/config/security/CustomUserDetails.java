package com.example.Projekat.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.Projekat.db.entity.UserEntity;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails
{
    private final UserEntity _userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return _userEntity.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword()
    {
        return _userEntity.getPassword();
    }

    @Override
    public String getUsername()
    {
        return _userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return !_userEntity.isExpired();
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return !_userEntity.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled()
    {
        return _userEntity.isEnabled();
    }

    public String getFirstName()
    {
        return _userEntity.getFirstName();
    }

    public String getLastName()
    {
        return _userEntity.getLastName();
    }

    public Set<String> getRoles()
    {
        return _userEntity.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .collect(Collectors.toSet());
    }

    public Long getId()
    {
        return _userEntity.getId();
    }

    public boolean shouldChangePassword()
    {
        return _userEntity.isShouldChangePassword();
    }
}


