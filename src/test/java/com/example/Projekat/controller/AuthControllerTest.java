package com.example.Projekat.controller;

import com.example.Projekat.config.SecurityConfig;
import com.example.Projekat.config.security.jwt.JwtTokenUtil;
import com.example.Projekat.dto.UserDto;
import com.example.Projekat.dto.auth.AuthRequest;
import com.example.Projekat.dto.auth.AuthResponse;
import com.example.Projekat.model.Role;
import com.example.Projekat.service.AuthService;
import com.example.Projekat.service.UserService;
import com.example.Projekat.dto.mapper.UserDtoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.EnumSet;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDtoMapper userDtoMapper;

    @MockBean
    private JwtTokenUtil jwtTokenUt;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAuthenticate() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("test")
                .password("pass")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("test")
                .roles(EnumSet.of(Role.GUEST))
                .enabled(true)
                .expired(false)
                .locked(false)
                .credentialsExpired(false)
                .shouldChangePassword(false)
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("dummy-token")
                .user(userDto)
                .build();

        Mockito.when(authService.generateAuthToken(any(), any()))
                .thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto dto = UserDto.builder()
                .id(1L)
                .username("newuser")
                .roles(EnumSet.of(Role.GUEST))
                .enabled(true)
                .expired(false)
                .locked(false)
                .credentialsExpired(false)
                .shouldChangePassword(false)
                .build();

        var user = com.example.Projekat.model.User.builder()
                .id(1L)
                .username("newuser")
                .roles(EnumSet.of(Role.GUEST))
                .enabled(true)
                .expired(false)
                .locked(false)
                .credentialsExpired(false)
                .shouldChangePassword(false)
                .build();

        Mockito.when(userService.create(any())).thenReturn(user);
        Mockito.when(userDtoMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/auth/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
