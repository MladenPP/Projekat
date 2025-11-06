package com.example.Projekat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class RestaurantDto {
    Long id;
    String name;
    String address;
}

