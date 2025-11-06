package com.example.Projekat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Item {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Long restaurantId;
}
