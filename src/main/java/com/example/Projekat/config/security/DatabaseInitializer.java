package com.example.Projekat.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.example.Projekat.db.entity.UserEntity;
import com.example.Projekat.db.repository.UserRepository;
import com.example.Projekat.model.Role;

import java.util.EnumSet;

@Configuration
@Slf4j
public class DatabaseInitializer
{
    @Value("${app.database.initial-user:admin}")
    private String _initialUser;

    @Value("${app.database.initial-user-password:admin.123}")
    private String _initialUserPassword;

    @Bean
    @Transactional
    public CommandLineRunner initDatabase(final UserRepository userRepository, final PasswordEncoder passwordEncoder)
    {
        return args -> initAdminUser(userRepository, passwordEncoder);
    }

    private void initAdminUser(final UserRepository userRepository, final PasswordEncoder passwordEncoder)
    {
        if (userRepository.findByUsername(_initialUser).isEmpty())
        {
            final UserEntity userEntity = new UserEntity();
            userEntity.setUsername(_initialUser);
            userEntity.setPassword(passwordEncoder.encode(_initialUserPassword));
            userEntity.setRoles(EnumSet.allOf(Role.class));
            userEntity.setFirstName("Mladen");
            userEntity.setLastName("Petrovic");
            userEntity.setEnabled(true);
            userEntity.setExpired(false);
            userEntity.setLocked(false);
            userEntity.setCredentialsExpired(false);
            userEntity.setShouldChangePassword(false);
            userEntity.setEmail("mladenp1@gmail.com");
            userEntity.setPhone("0601888966");

            userRepository.save(userEntity);
            log.debug("Admin user created");
        }
        else
        {
            log.debug("Admin user already exists");
        }
    }
}

