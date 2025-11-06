package com.example.Projekat.dto.mapper;

import org.springframework.stereotype.Component;
import com.example.Projekat.dto.RestaurantDto;
import com.example.Projekat.model.Restaurant;

@Component
public class RestaurantDtoMapper {

    public RestaurantDto toDto(final Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .build();
    }

    public Restaurant fromDto(final RestaurantDto dto) {
        return Restaurant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .address(dto.getAddress())
                .build();
    }
}
