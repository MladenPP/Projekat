package com.example.Projekat.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Projekat.config.security.ChangePasswordRequest;
import com.example.Projekat.db.entity.UserEntity;
import com.example.Projekat.db.entity.mapper.UserEntityMapper;
import com.example.Projekat.db.repository.UserRepository;
import com.example.Projekat.model.Role;
import com.example.Projekat.model.User;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository _userRepository;
    private final UserEntityMapper _userEntityMapper;
    private final PasswordEncoder _passwordEncoder;

    public User getUserById(final Long id) {
        return _userRepository.findById(id)
                .map(_userEntityMapper::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " does not exist!"));
    }

    public List<User> getAll() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Set<Role> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::fromString)
                .collect(Collectors.toSet());

        if (roles.contains(Role.ADMIN)) {
            return _userRepository.findAll().stream()
                    .map(_userEntityMapper::fromEntity)
                    .toList();
        }

        return _userRepository
                .findByUsername(authentication.getName())
                .map(_userEntityMapper::fromEntity)
                .stream()
                .toList();
    }

    public User create(final User user) {
        if (_userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists!");
        }

        if (_userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists!");
        }

        if (_userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new IllegalArgumentException("User with phone " + user.getPhone() + " already exists!");
        }

        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        validateEmail(user.getEmail(), null);
        validatePhone(user.getPhone(), null);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());

        Set<Role> allowedRoles = Set.of();

        if (isAuthenticated) {
            final Set<Role> callerRoles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(Role::fromString)
                    .collect(Collectors.toSet());

            if (callerRoles.contains(Role.ADMIN)) {
                allowedRoles = Set.of(Role.ADMIN, Role.MANAGER, Role.GUEST);
            } else if (callerRoles.contains(Role.MANAGER)) {
                allowedRoles = Set.of(Role.MANAGER, Role.GUEST);
            }

            if (user.getRoles() != null && !user.getRoles().isEmpty() && !allowedRoles.isEmpty()) {
                Set<Role> filteredRoles = user.getRoles().stream()
                        .filter(allowedRoles::contains)
                        .collect(Collectors.toSet());
                if (filteredRoles.isEmpty()) filteredRoles = Set.of(Role.GUEST);
                user.setRoles(filteredRoles);
            } else {
                user.setRoles(Set.of(Role.GUEST));
            }

            if (!callerRoles.contains(Role.ADMIN)) {
                user.setExpired(false);
                user.setLocked(false);
                user.setCredentialsExpired(false);
                user.setShouldChangePassword(false);
            }

        } else {
            user.setRoles(Set.of(Role.GUEST));
            user.setExpired(false);
            user.setLocked(false);
            user.setCredentialsExpired(false);
            user.setShouldChangePassword(false);
        }

        final User userWithEncodedPassword = user.toBuilder()
                .password(_passwordEncoder.encode(user.getPassword()))
                .enabled(true)
                .build();

        return _userEntityMapper.fromEntity(
                _userRepository.save(_userEntityMapper.toEntity(userWithEncodedPassword))
        );
    }

    public User update(final Long id, final User updatedUser, final boolean isAdmin) {
        final UserEntity existingUser = _userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " does not exist!"));

        String oldEmail = existingUser.getEmail();
        String oldPhone = existingUser.getPhone();

        if (!isAdmin) {
            assertUpdateValidId(id, updatedUser);
            assertCanUpdateUsername(id, updatedUser.getUsername(), existingUser.getUsername());

            updatedUser.setRoles(existingUser.getRoles());

            if (updatedUser.getPassword() == null || updatedUser.getPassword().isBlank()) {
                updatedUser.setPassword(existingUser.getPassword());
            } else {
                updatedUser.setPassword(_passwordEncoder.encode(updatedUser.getPassword()));
            }

            if (!StringUtils.hasText(updatedUser.getUsername()))
                updatedUser.setUsername(existingUser.getUsername());

            if (!StringUtils.hasText(updatedUser.getEmail()))
                updatedUser.setEmail(existingUser.getEmail());

            if (!StringUtils.hasText(updatedUser.getPhone()))
                updatedUser.setPhone(existingUser.getPhone());

            if (!StringUtils.hasText(updatedUser.getFirstName()))
                updatedUser.setFirstName(existingUser.getFirstName());

            if (!StringUtils.hasText(updatedUser.getLastName()))
                updatedUser.setLastName(existingUser.getLastName());

            validateEmail(updatedUser.getEmail(), oldEmail);
            validatePhone(updatedUser.getPhone(), oldPhone);

            updatedUser.setExpired(false);
            updatedUser.setLocked(false);
            updatedUser.setCredentialsExpired(false);
            updatedUser.setShouldChangePassword(false);

            return _userEntityMapper.fromEntity(
                    _userRepository.save(_userEntityMapper.toEntity(updatedUser))
            );
        }

        boolean changedSomethingElse =
                !Objects.equals(updatedUser.getUsername(), existingUser.getUsername()) ||
                        !Objects.equals(updatedUser.getFirstName(), existingUser.getFirstName()) ||
                        !Objects.equals(updatedUser.getLastName(), existingUser.getLastName()) ||
                        !Objects.equals(updatedUser.getEmail(), existingUser.getEmail()) ||
                        !Objects.equals(updatedUser.getPhone(), existingUser.getPhone()) ||
                        (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank());

        if (changedSomethingElse) {
            throw new SecurityException("Admin can only modify user roles, not other fields.");
        }

        existingUser.setRoles(updatedUser.getRoles());
        existingUser.setEnabled(updatedUser.isEnabled());
        existingUser.setExpired(updatedUser.isExpired());
        existingUser.setLocked(updatedUser.isLocked());
        existingUser.setCredentialsExpired(updatedUser.isCredentialsExpired());
        existingUser.setShouldChangePassword(updatedUser.isShouldChangePassword());

        return _userEntityMapper.fromEntity(_userRepository.save(existingUser));
    }

    public void assertUpdateValidId(final Long id, final User updatedUser) {
        final Long userId = updatedUser.getId();
        if (userId == null || !userId.equals(id)) {
            throw new IllegalArgumentException("User id does not match!");
        }
    }

    private void assertCanUpdateUsername(final Long id, final String newUsername, final String existingUsername) {
        if ((newUsername != null) && !newUsername.equals(existingUsername)) {
            if (_userRepository.findByUsername(newUsername).isPresent()) {
                throw new IllegalArgumentException(
                        "Cannot update user %s as user with username %s already exists"
                                .formatted(id, newUsername));
            }
        }
    }

    private boolean canUpdateRoles() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Set<Role> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::fromString)
                .collect(Collectors.toSet());

        return roles.contains(Role.ADMIN) || roles.contains(Role.MANAGER);
    }

    public void delete(final Long id) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String loggedInUsername = authentication.getName();

        final UserEntity loggedInUser = _userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        final UserEntity targetUser = _userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (loggedInUser.getId().equals(targetUser.getId())) {
            _userRepository.deleteById(id);
            return;
        }

        boolean isAdmin = loggedInUser.getRoles().contains(Role.ADMIN);
        if (!isAdmin) {
            throw new SecurityException("Only admins can delete other users");
        }

        if (targetUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Admin cannot delete another admin");
        }

        _userRepository.deleteById(id);
    }

    @Transactional
    public User changePassword(final @Valid ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getNewPasswordConfirmed())) {
            throw new IllegalArgumentException("New password and confirmed password do not match");
        }

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Cannot change password as no authentication is available");
        }

        final String username = authentication.getName();

        final User user = _userRepository.findByUsername(username)
                .map(_userEntityMapper::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("User with username '" + username + "' does not exist!"));

        if (request.getOldPassword() != null
                && !_passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password do not match");
        }

        final User updatedUser = user.toBuilder()
                .password(_passwordEncoder.encode(request.getNewPassword()))
                .shouldChangePassword(false)
                .build();

        return _userEntityMapper.fromEntity(_userRepository.save(_userEntityMapper.toEntity(updatedUser)));
    }

    private void validatePhone(String phone, String oldPhone) {
        if (!StringUtils.hasText(phone) || !phone.matches("\\d{9,13}")) {
            throw new IllegalArgumentException("Phone must contain only digits and have at least 9 characters");
        }

        _userRepository.findByPhone(phone)
                .filter(u -> !phone.equals(oldPhone))
                .ifPresent(u -> { throw new IllegalArgumentException("Phone already exists"); });
    }

    private void validateEmail(String email, String oldEmail) {
        if (!StringUtils.hasText(email) || !email.matches("^[\\w-.]+@[\\w-]+\\.(com|rs|edu)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        _userRepository.findByEmail(email)
                .filter(u -> !email.equals(oldEmail))
                .ifPresent(u -> { throw new IllegalArgumentException("Email already exists"); });
    }
}
