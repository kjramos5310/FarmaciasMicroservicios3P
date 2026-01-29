package com.espe.oauth_server.oauth.server.controller;

import com.espe.oauth_server.oauth.server.dto.CreateUserRequest;
import com.espe.oauth_server.oauth.server.dto.UserDTO;
import com.espe.oauth_server.oauth.server.model.User;
import com.espe.oauth_server.oauth.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        User user = new User(request.getUsername(), request.getPassword(), request.getEmail());
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(convertToDTO(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            User user = userService.assignRoleToUser(userId, roleId);
            return ResponseEntity.ok(convertToDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            User user = userService.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok(convertToDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
        );
    }
}
