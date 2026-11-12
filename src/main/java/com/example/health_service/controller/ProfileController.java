package com.example.health_service.controller;

import com.example.health_service.dto.ProfileUpdateRequest;
import com.example.health_service.dto.UserDto;
import com.example.health_service.model.User;
import com.example.health_service.repository.IUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final IUserRepository userRepository;

    public ProfileController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail()
        ));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateMyProfile(@AuthenticationPrincipal User currentUser,
                                                   @Valid @RequestBody ProfileUpdateRequest request) {
        currentUser.setName(request.getName());
        User saved = userRepository.save(currentUser);
        return ResponseEntity.ok(new UserDto(saved.getId(), saved.getName(), saved.getEmail()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserDto> getProfileById(@PathVariable Long id,
                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<UserDto> updateProfileById(@PathVariable Long id,
                                                     @AuthenticationPrincipal User currentUser,
                                                     @Valid @RequestBody ProfileUpdateRequest request) {
        currentUser.setName(request.getName());
        User saved = userRepository.save(currentUser);
        return ResponseEntity.ok(new UserDto(saved.getId(), saved.getName(), saved.getEmail()));
    }
}