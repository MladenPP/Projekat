package com.example.Projekat.controller;

import com.example.Projekat.config.security.jwt.JwtTokenUtil;
import com.example.Projekat.dto.RestaurantDto;
import com.example.Projekat.model.Restaurant;
import com.example.Projekat.service.RestaurantService;
import com.example.Projekat.dto.mapper.RestaurantDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private RestaurantDtoMapper restaurantDtoMapper;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

        Mockito.when(jwtTokenUtil.validateToken(anyString(), any()))
                .thenReturn(true);

        Mockito.when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new User("admin", "password",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    void testGetAllRestaurants_publicEndpoint() throws Exception {
        Restaurant restaurant = new Restaurant(1L, "Resto", "Address");
        RestaurantDto dto = RestaurantDto.builder()
                .id(1L)
                .name("Resto")
                .address("Address")
                .build();

        Mockito.when(restaurantService.getAll()).thenReturn(List.of(restaurant));
        Mockito.when(restaurantDtoMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void testCreateRestaurant_managerAccess() throws Exception {
        Restaurant restaurant = new Restaurant(1L, "Resto", "Address");
        RestaurantDto dto = RestaurantDto.builder()
                .name("Resto")
                .address("Address")
                .build();

        Mockito.when(restaurantDtoMapper.fromDto(any())).thenReturn(restaurant);
        Mockito.when(restaurantService.create(any())).thenReturn(restaurant);
        Mockito.when(restaurantDtoMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"GUEST"})
    void testCreateRestaurant_forbiddenAccess() throws Exception {
        RestaurantDto dto = RestaurantDto.builder()
                .name("Forbidden")
                .address("Test")
                .build();

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
