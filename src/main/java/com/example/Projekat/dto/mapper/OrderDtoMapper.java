package com.example.Projekat.dto.mapper;

import com.example.Projekat.dto.OrderDto;
import com.example.Projekat.dto.OrderItemDto;
import com.example.Projekat.model.Order;
import com.example.Projekat.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderDtoMapper {

    public OrderDto toDto(Order order) {
        if (order == null) return null;

        Set<OrderItemDto> items = null;
        if (order.getOrderItems() != null) {
            items = order.getOrderItems().stream()
                    .map(oi -> new OrderItemDto(oi.getItemId(), oi.getQuantity()))
                    .collect(Collectors.toSet());
        }

        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getOrderDate(),
                order.getStatus(),
                items
        );
    }

    public Order fromDto(OrderDto dto) {
        if (dto == null) return null;

        Set<OrderItem> items = null;
        if (dto.getOrderItems() != null) {
            items = dto.getOrderItems().stream()
                    .map(oi -> new OrderItem(oi.getItemId(), oi.getQuantity()))
                    .collect(Collectors.toSet());
        }

        return new Order(
                dto.getId(),
                dto.getUserId(),
                dto.getOrderDate(),
                dto.getStatus(),
                items
        );
    }
}
