package com.example.Projekat.service;

import com.example.Projekat.db.entity.OrderEntity;
import com.example.Projekat.db.entity.OrderItemEntity;
import com.example.Projekat.db.entity.UserEntity;
import com.example.Projekat.db.repository.ItemRepository;
import com.example.Projekat.db.repository.OrderRepository;
import com.example.Projekat.db.repository.UserRepository;
import com.example.Projekat.model.Order;
import com.example.Projekat.model.OrderItem;
import com.example.Projekat.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Order> getAll() {
        UserEntity loggedUser = getLoggedUser();

        boolean isAdminOrManager = loggedUser.getRoles().stream()
                .anyMatch(r -> r == Role.ADMIN || r == Role.MANAGER);

        List<OrderEntity> orders;

        if (isAdminOrManager) {
            orders = orderRepository.findAllWithItems();
        } else {
            orders = orderRepository.findByUserIdWithItems(loggedUser.getId());
        }

        return orders.stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Order getById(Long id) {
        UserEntity loggedUser = getLoggedUser();

        boolean isAdminOrManager = loggedUser.getRoles().stream()
                .anyMatch(r -> r == Role.ADMIN || r == Role.MANAGER);

        OrderEntity entity = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        if (!isAdminOrManager && !entity.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToModel(entity);
    }

    @Transactional
    public Order create(Order order) {

        Order checkedOrder = inputCheck(order);

        OrderEntity orderEntity = new OrderEntity();

        UserEntity user = userRepository.findById(checkedOrder.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + checkedOrder.getUserId()));
        orderEntity.setUser(user);

        orderEntity.setOrderDate(
                checkedOrder.getOrderDate() != null ? checkedOrder.getOrderDate() : LocalDateTime.now()
        );
        orderEntity.setStatus(checkedOrder.getStatus());

        Set<OrderItemEntity> orderItems = checkedOrder.getOrderItems().stream().map(item -> {
            OrderItemEntity entity = new OrderItemEntity();
            entity.setItem(itemRepository.getReferenceById(item.getItemId()));
            entity.setQuantity(item.getQuantity());
            entity.setOrder(orderEntity);
            return entity;
        }).collect(Collectors.toSet());

        orderEntity.setOrderItems(orderItems);

        OrderEntity saved = orderRepository.save(orderEntity);
        return mapToModel(saved);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    private Order inputCheck(Order order) {
        UserEntity loggedUser = getLoggedUser();

        boolean isAdminOrManager = loggedUser.getRoles().stream()
                .anyMatch(r -> r == Role.ADMIN || r == Role.MANAGER);

        order.setUserId(loggedUser.getId());

        if (!isAdminOrManager) {
            order.setStatus("PENDING");
            order.setOrderDate(LocalDateTime.now());
        }

        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        for (OrderItem item : order.getOrderItems()) {
            if (item.getQuantity() < 0 || item.getQuantity() > 10) {
                throw new RuntimeException("Invalid quantity for itemId " + item.getItemId() + ": must be between 0 and 11");
            }

            if (!itemRepository.existsById(item.getItemId())) {
                throw new RuntimeException("Item not found: " + item.getItemId());
            }
        }

        return order;
    }

    private Order mapToModel(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setUserId(entity.getUser().getId());
        order.setOrderDate(entity.getOrderDate());
        order.setStatus(entity.getStatus());

        Set<OrderItem> items = new HashSet<>();
        if (entity.getOrderItems() != null) {
            for (OrderItemEntity oi : entity.getOrderItems()) {
                OrderItem oiModel = new OrderItem();
                oiModel.setItemId(oi.getItem().getId());
                oiModel.setQuantity(oi.getQuantity());
                items.add(oiModel);
            }
        }
        order.setOrderItems(items);
        return order;
    }

    private UserEntity getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Logged user not found"));
    }
}
