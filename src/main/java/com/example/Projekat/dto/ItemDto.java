package com.example.Projekat.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Long restaurantId;
}

