package com.example.Projekat.controller;

import com.example.Projekat.dto.UserDto;
import com.example.Projekat.model.User;
import com.example.Projekat.service.UserService;
import com.example.Projekat.dto.mapper.UserDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser(username = "admin", roles = "ADMIN")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDtoMapper userDtoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllUsers() throws Exception {
        User user = new User(
                1L, "user", "pass", "First", "Last", "email@test.com", "123456",
                true, false, false, false, false,
                EnumSet.of(com.example.Projekat.model.Role.GUEST)
        );

        Mockito.when(userService.getAll()).thenReturn(List.of(user));
        Mockito.when(userDtoMapper.toDto(any())).thenReturn(UserDto.builder()
                .id(1L)
                .username("user")
                .roles(EnumSet.of(com.example.Projekat.model.Role.GUEST))
                .build());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserDetails() throws Exception {
        User user = new User(
                1L, "user", "pass", "First", "Last", "email@test.com", "123456",
                true, false, false, false, false,
                EnumSet.of(com.example.Projekat.model.Role.GUEST)
        );

        UserDto dto = UserDto.builder()
                .id(1L)
                .username("user")
                .roles(EnumSet.of(com.example.Projekat.model.Role.GUEST))
                .build();

        Mockito.when(userService.update(anyLong(), any(), anyBoolean())).thenReturn(user);
        Mockito.when(userDtoMapper.toDto(any())).thenReturn(dto);
        Mockito.when(userDtoMapper.fromDto(any())).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
