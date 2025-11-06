package com.example.Projekat.db.entity.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.Projekat.db.entity.ItemEntity;
import com.example.Projekat.db.entity.RestaurantEntity;
import com.example.Projekat.model.Item;

@Component
@RequiredArgsConstructor
public class ItemEntityMapper {

    public ItemEntity toEntity(final Item item) {
        RestaurantEntity restaurant = null;
        if (item.getRestaurantId() != null) {
            restaurant = RestaurantEntity.builder().id(item.getRestaurantId()).build();
        }

        return ItemEntity.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .restaurant(restaurant)
                .build();
    }

    public Item fromEntity(final ItemEntity entity) {
        return Item.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .restaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null)
                .build();
    }
}
