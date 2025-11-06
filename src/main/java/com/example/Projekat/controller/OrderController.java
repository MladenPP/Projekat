package com.example.Projekat.controller;

import com.example.Projekat.dto.OrderDto;
import com.example.Projekat.dto.mapper.OrderDtoMapper;
import com.example.Projekat.logging.LogUserAction;
import com.example.Projekat.model.Order;
import com.example.Projekat.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<OrderDto> getAllOrders() {
        return orderService.getAll().stream()
                .map(orderDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public OrderDto getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return orderDtoMapper.toDto(order);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @LogUserAction("Order created")
    public OrderDto createOrder(@RequestBody OrderDto orderDto) {
        Order order = orderDtoMapper.fromDto(orderDto);
        Order created = orderService.create(order);
        return orderDtoMapper.toDto(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @LogUserAction("Order deleted")
    public void deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
    }
}
