package com.example.Projekat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Restaurant {
    Long id;
    String name;
    String address;
}
