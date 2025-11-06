package com.example.Projekat.db.repository;

import com.example.Projekat.db.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Query("SELECT o FROM OrderEntity o")
    List<OrderEntity> findAllWithItems();

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Query("SELECT o FROM OrderEntity o WHERE o.user.id = :userId")
    List<OrderEntity> findByUserIdWithItems(Long userId);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Query("SELECT o FROM OrderEntity o WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(Long id);
}
