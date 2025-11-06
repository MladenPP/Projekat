package com.example.Projekat.db.entity.mapper;

import org.springframework.stereotype.Component;
import com.example.Projekat.db.entity.RestaurantEntity;
import com.example.Projekat.model.Restaurant;

@Component
public class RestaurantEntityMapper {

    public RestaurantEntity toEntity(final Restaurant restaurant) {
        return RestaurantEntity.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .build();
    }

    public Restaurant fromEntity(final RestaurantEntity entity) {
        return Restaurant.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .build();
    }
}

