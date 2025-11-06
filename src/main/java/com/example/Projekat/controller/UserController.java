package com.example.Projekat.controller;

import com.example.Projekat.logging.LogUserAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.Projekat.config.security.ChangePasswordRequest;
import com.example.Projekat.model.User;
import com.example.Projekat.service.UserService;
import com.example.Projekat.dto.UserDto;
import com.example.Projekat.dto.mapper.UserDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService _userService;
    private final UserDtoMapper _userDtoMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<UserDto> getAllUsers()
    {
        return _userService.getAll().stream().map(_userDtoMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public UserDto getUserById(@PathVariable final Long id)
    {
        return _userDtoMapper.toDto(_userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    @LogUserAction("User updated")
    public UserDto updateUserDetails(@PathVariable final Long id, @RequestBody @Valid final UserDto userDetails)
    {
        final User updatedUser = _userService.update(id, _userDtoMapper.fromDto(userDetails),false);
        return _userDtoMapper.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteUser(@PathVariable final Long id)
    {
        _userService.delete(id);
    }

    @PutMapping("/change-password")
    @PreAuthorize("#id == authentication.principal.id")
    @LogUserAction("User changed password")
    public UserDto changePassword(@RequestBody @Valid final ChangePasswordRequest changePasswordRequest)
    {
        final User updatedUser = _userService.changePassword(changePasswordRequest);
        return _userDtoMapper.toDto(updatedUser);
    }
}

