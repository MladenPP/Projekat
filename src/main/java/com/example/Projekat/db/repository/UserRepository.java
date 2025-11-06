package com.example.Projekat.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Projekat.db.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>
{

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);
}
