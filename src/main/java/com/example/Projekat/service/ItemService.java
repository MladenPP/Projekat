package com.example.Projekat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.Projekat.db.repository.ItemRepository;
import com.example.Projekat.db.repository.RestaurantRepository;
import com.example.Projekat.db.entity.mapper.ItemEntityMapper;
import com.example.Projekat.model.Item;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository _itemRepository;
    private final ItemEntityMapper _itemEntityMapper;
    private final RestaurantRepository _restaurantRepository;

    public List<Item> getAllItems() {
        return _itemRepository.findAll()
                .stream()
                .map(_itemEntityMapper::fromEntity)
                .toList();
    }

    public List<Item> findAllByRestaurantId(final Long restaurantId) {
        return _itemRepository.findAllByRestaurantId(restaurantId)
                .stream()
                .map(_itemEntityMapper::fromEntity)
                .toList();
    }

    public Item findByIdAndRestaurantId(final Long itemId, final Long restaurantId) {
        return _itemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .map(_itemEntityMapper::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item with id " + itemId + " not found for restaurant " + restaurantId));
    }

    public Item create(final Long restaurantId, final Item item) {
        // --- VALIDACIJE ---
        if (!_restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("Restaurant with id " + restaurantId + " not found!");
        }

        if (!StringUtils.hasText(item.getName())) {
            throw new IllegalArgumentException("Item name cannot be empty!");
        }

        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Item price cannot be negative!");
        }

        final Item newItem = item.toBuilder()
                .restaurantId(restaurantId)
                .build();

        return _itemEntityMapper.fromEntity(
                _itemRepository.save(_itemEntityMapper.toEntity(newItem))
        );
    }

    public Item update(final Long restaurantId, final Long itemId, final Item item) {
        final Item existing = findByIdAndRestaurantId(itemId, restaurantId);

        // --- VALIDACIJE ---
        if (!_restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("Restaurant with id " + restaurantId + " not found!");
        }

        String newName = StringUtils.hasText(item.getName()) ? item.getName() : existing.getName();
        BigDecimal newPrice = (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) >= 0) ? item.getPrice() : existing.getPrice();
        String newDescription = StringUtils.hasText(item.getDescription()) ? item.getDescription() : existing.getDescription();

        final Item updated = existing.toBuilder()
                .name(newName)
                .description(newDescription)
                .price(newPrice)
                .restaurantId(restaurantId)
                .build();

        return _itemEntityMapper.fromEntity(
                _itemRepository.save(_itemEntityMapper.toEntity(updated))
        );
    }

    public Page<Item> searchByName(String name, int page, int size, String sortBy, boolean asc) {
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return _itemRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(_itemEntityMapper::fromEntity);
    }

    public Page<Item> getAllPaginated(int page, int size, String sortBy, boolean asc) {
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return _itemRepository.findAll(pageable)
                .map(_itemEntityMapper::fromEntity);
    }


    public void delete(final Long restaurantId, final Long itemId) {
        final Item existing = findByIdAndRestaurantId(itemId, restaurantId);
        _itemRepository.deleteById(existing.getId());
    }
}
