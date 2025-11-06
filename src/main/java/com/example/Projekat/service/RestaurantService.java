package com.example.Projekat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.Projekat.db.repository.RestaurantRepository;
import com.example.Projekat.db.entity.mapper.RestaurantEntityMapper;
import com.example.Projekat.model.Restaurant;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEntityMapper restaurantEntityMapper;

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantEntityMapper::fromEntity)
                .toList();
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantEntityMapper::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with id " + id + " not found"));
    }

    public Restaurant create(Restaurant restaurant) {

        if (!StringUtils.hasText(restaurant.getName())) {
            throw new IllegalArgumentException("Restaurant name cannot be empty!");
        }
        if (!StringUtils.hasText(restaurant.getAddress())) {
            throw new IllegalArgumentException("Restaurant address cannot be empty!");
        }

        if (restaurantRepository.existsByName(restaurant.getName())) {
            throw new IllegalArgumentException("Restaurant with name " + restaurant.getName() + " already exists!");
        }

        return restaurantEntityMapper.fromEntity(
                restaurantRepository.save(restaurantEntityMapper.toEntity(restaurant))
        );
    }

    public Restaurant update(Long id, Restaurant updated) {
        var existingEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with id " + id + " does not exist!"));

        String newName = StringUtils.hasText(updated.getName()) ? updated.getName() : existingEntity.getName();
        String newAddress = StringUtils.hasText(updated.getAddress()) ? updated.getAddress() : existingEntity.getAddress();

        if (!newName.equals(existingEntity.getName()) && restaurantRepository.existsByName(newName)) {
            throw new IllegalArgumentException("Restaurant with name " + newName + " already exists!");
        }

        updated.setId(id);
        updated.setName(newName);
        updated.setAddress(newAddress);

        return restaurantEntityMapper.fromEntity(
                restaurantRepository.save(restaurantEntityMapper.toEntity(updated))
        );
    }

    public Page<Restaurant> searchByName(String name, int page, int size, String sortBy, boolean asc) {
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return restaurantRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(restaurantEntityMapper::fromEntity);
    }

    public Page<Restaurant> getAllPaginated(int page, int size, String sortBy, boolean asc) {
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return restaurantRepository.findAll(pageable)
                .map(restaurantEntityMapper::fromEntity);
    }

    public void delete(Long id) {
        restaurantRepository.deleteById(id);
    }
}
