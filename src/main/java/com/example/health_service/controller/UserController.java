package com.example.health_service.controller;

import com.example.health_service.dto.UserDto;
import com.example.health_service.model.User;
import com.example.health_service.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        User saved = userRepository.save(new User(userDto.getName(), userDto.getEmail()));
        log.info("User created id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserDto(saved.getId(), saved.getName(), saved.getEmail()));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail()))
                .collect(Collectors.toList());
        log.info("Users listed count={}", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> {
                    log.info("User fetched id={}", id);
                    return ResponseEntity.ok(new UserDto(u.getId(), u.getName(), u.getEmail()));
                })
                .orElseGet(() -> {
                    log.warn("User not found id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setName(userDto.getName());
                    existing.setEmail(userDto.getEmail());
                    User updated = userRepository.save(existing);
                    log.info("User updated id={}", updated.getId());
                    return ResponseEntity.ok(new UserDto(updated.getId(), updated.getName(), updated.getEmail()));
                })
                .orElseGet(() -> {
                    log.warn("User not found for update id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User deleted id={}", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("User not found for delete id={}", id);
        return ResponseEntity.notFound().build();
    }
}