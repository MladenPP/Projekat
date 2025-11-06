package com.example.Projekat.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Projekat.db.entity.ItemEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findAllByRestaurantId(Long restaurantId);
    Optional<ItemEntity> findByIdAndRestaurantId(Long itemId, Long restaurantId);
    Page<ItemEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

