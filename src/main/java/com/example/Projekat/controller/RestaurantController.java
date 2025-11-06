package com.example.Projekat.controller;

import com.example.Projekat.logging.LogUserAction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.Projekat.service.RestaurantService;
import com.example.Projekat.service.ItemService;
import com.example.Projekat.dto.mapper.RestaurantDtoMapper;
import com.example.Projekat.dto.mapper.ItemDtoMapper;
import com.example.Projekat.dto.RestaurantDto;
import com.example.Projekat.dto.ItemDto;
import com.example.Projekat.model.Restaurant;
import com.example.Projekat.model.Item;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantDtoMapper restaurantDtoMapper;

    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;

    // ---------------------------
    // RESTAURANTS
    // ---------------------------

    @GetMapping
    public List<RestaurantDto> getAll() {
        return restaurantService.getAll()
                .stream()
                .map(restaurantDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public RestaurantDto getById(@PathVariable Long id) {
        return restaurantDtoMapper.toDto(restaurantService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @LogUserAction("Manager added restaurant")
    public RestaurantDto create(@RequestBody RestaurantDto dto) {
        Restaurant restaurant = restaurantDtoMapper.fromDto(dto);
        return restaurantDtoMapper.toDto(restaurantService.create(restaurant));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @LogUserAction("Manager updated restaurant")
    public RestaurantDto update(@PathVariable Long id, @RequestBody RestaurantDto dto) {
        Restaurant restaurant = restaurantDtoMapper.fromDto(dto);
        return restaurantDtoMapper.toDto(restaurantService.update(id, restaurant));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @LogUserAction("Manager deleted restaurant")
    public void delete(@PathVariable Long id) {
        restaurantService.delete(id);
    }

    @GetMapping("/search")
    public Page<RestaurantDto> searchRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {

        if (name == null || name.isBlank()) {

            return restaurantService.getAllPaginated(page, size, sortBy, asc)
                    .map(restaurantDtoMapper::toDto);
        }

        return restaurantService.searchByName(name, page, size, sortBy, asc)
                .map(restaurantDtoMapper::toDto);
    }

    // ---------------------------
    // ITEMS
    // ---------------------------

    @GetMapping("/{restaurantId}/items")
    public List<ItemDto> getAllItems(@PathVariable Long restaurantId) {
        return itemService.findAllByRestaurantId(restaurantId)
                .stream()
                .map(itemDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/{restaurantId}/items/{itemId}")
    public ItemDto getItemById(@PathVariable Long restaurantId, @PathVariable Long itemId) {
        return itemDtoMapper.toDto(itemService.findByIdAndRestaurantId(itemId, restaurantId));
    }

    @PostMapping("/{restaurantId}/items")
    @PreAuthorize("hasRole('MANAGER')")
    @LogUserAction("Manager added item")
    public ItemDto createItem(@PathVariable Long restaurantId, @RequestBody ItemDto dto) {
        Item item = itemDtoMapper.fromDto(dto);
        return itemDtoMapper.toDto(itemService.create(restaurantId, item));
    }

    @PutMapping("/{restaurantId}/items/{itemId}")
    @PreAuthorize("hasRole('MANAGER')")
    @LogUserAction("Manager updated item")
    public ItemDto updateItem(@PathVariable Long restaurantId, @PathVariable Long itemId, @RequestBody ItemDto dto) {
        Item item = itemDtoMapper.fromDto(dto);
        return itemDtoMapper.toDto(itemService.update(restaurantId, itemId, item));
    }

    @DeleteMapping("/{restaurantId}/items/{itemId}")
    @PreAuthorize("hasRole('MANAGER')")
    @LogUserAction("Manager deleted item")
    public void deleteItem(@PathVariable Long restaurantId, @PathVariable Long itemId) {
        itemService.delete(restaurantId, itemId);
    }

    @GetMapping("/items/search")
    public Page<ItemDto> searchItems(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {

        if (name == null || name.isBlank()) {

            return itemService.getAllPaginated(page, size, sortBy, asc)
                    .map(itemDtoMapper::toDto);
        }

        return itemService.searchByName(name, page, size, sortBy, asc)
                .map(itemDtoMapper::toDto);
    }

    @GetMapping("/items")
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems()
                .stream()
                .map(itemDtoMapper::toDto)
                .toList();
    }
}

