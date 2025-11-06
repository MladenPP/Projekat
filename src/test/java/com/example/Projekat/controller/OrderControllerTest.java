package com.example.Projekat.controller;

import com.example.Projekat.dto.OrderDto;
import com.example.Projekat.model.Order;
import com.example.Projekat.model.OrderItem;
import com.example.Projekat.service.OrderService;
import com.example.Projekat.dto.mapper.OrderDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderDtoMapper orderDtoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user", roles = {"GUEST"})
    void testGetAllOrders_authenticatedUser() throws Exception {
        Set<OrderItem> items = new HashSet<>();
        items.add(new OrderItem(1L, 2));

        Order order = new Order(1L, 1L, LocalDateTime.now(), "PENDING", items);
        OrderDto dto = OrderDto.builder()
                .id(1L)
                .userId(1L)
                .status("PENDING")
                .build();

        Mockito.when(orderService.getAll()).thenReturn(List.of(order));
        Mockito.when(orderDtoMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllOrders_unauthorized() throws Exception {
        // Bez korisnika → 401 Unauthorized
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateOrder_authenticatedUser() throws Exception {
        Set<OrderItem> items = new HashSet<>();
        items.add(new OrderItem(1L, 2));

        Order order = new Order(1L, 1L, LocalDateTime.now(), "PENDING", items);
        OrderDto dto = OrderDto.builder()
                .id(1L)
                .userId(1L)
                .status("PENDING")
                .build();

        Mockito.when(orderDtoMapper.fromDto(any())).thenReturn(order);
        Mockito.when(orderService.create(any())).thenReturn(order);
        Mockito.when(orderDtoMapper.toDto(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()) // dodaje CSRF token
                        .with(user("guest").roles("GUEST"))) // simulacija autentifikovanog korisnika
                .andExpect(status().isOk());
    }
}
