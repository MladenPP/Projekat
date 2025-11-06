package com.example.Projekat.dto.mapper;

import org.springframework.stereotype.Component;
import com.example.Projekat.model.Item;
import com.example.Projekat.dto.ItemDto;

@Component
public class ItemDtoMapper {

    public ItemDto toDto(final Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .restaurantId(item.getRestaurantId())
                .build();
    }

    public Item fromDto(final ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .restaurantId(dto.getRestaurantId())
                .build();
    }
}
