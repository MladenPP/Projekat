package com.example.Projekat.db.entity.mapper;

import com.example.Projekat.db.entity.OrderEntity;
import com.example.Projekat.db.entity.OrderItemEntity;
import com.example.Projekat.model.Order;
import com.example.Projekat.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

    public Order fromEntity(OrderEntity entity) {
        if (entity == null) return null;

        Set<OrderItem> items = null;
        if (entity.getOrderItems() != null) {
            items = entity.getOrderItems().stream()
                    .map(oi -> new OrderItem(oi.getItem().getId(), oi.getQuantity()))
                    .collect(Collectors.toSet());
        }

        return new Order(
                entity.getId(),
                entity.getUser().getId(),
                entity.getOrderDate(),
                entity.getStatus(),
                items
        );
    }

    public OrderEntity toEntity(Order order) {
        if (order == null) return null;

        OrderEntity entity = new OrderEntity();
        entity.setOrderDate(order.getOrderDate());
        entity.setStatus(order.getStatus());

        if (order.getOrderItems() != null) {
            Set<OrderItemEntity> items = order.getOrderItems().stream()
                    .map(oi -> {
                        OrderItemEntity oie = new OrderItemEntity();
                        oie.setQuantity(oi.getQuantity());
                        return oie;
                    })
                    .collect(Collectors.toSet());
            entity.setOrderItems(items);
        }

        return entity;
    }
}
