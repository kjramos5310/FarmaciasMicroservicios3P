package com.espe.oauth_server.oauth.server.controller;

import com.espe.oauth_server.oauth.server.dto.AuthResponse;
import com.espe.oauth_server.oauth.server.dto.LoginRequest;
import com.espe.oauth_server.oauth.server.dto.RegisterRequest;
import com.espe.oauth_server.oauth.server.model.Role;
import com.espe.oauth_server.oauth.server.model.User;
import com.espe.oauth_server.oauth.server.service.RoleService;
import com.espe.oauth_server.oauth.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthController(UserService userService, RoleService roleService, 
                         PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Crear usuario
            User user = new User(request.getUsername(), request.getPassword(), request.getEmail());
            User createdUser = userService.createUser(user);

            // Verificar si es el primer usuario
            long totalUsers = userService.getAllUsers().size();
            
            if (totalUsers == 1) {
                // Primer usuario: asignar rol ADMIN
                Role adminRole = roleService.getRoleByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
                userService.assignRoleToUser(createdUser.getId(), adminRole.getId());
                
                AuthResponse response = new AuthResponse(
                        "¡Primer usuario registrado como ADMINISTRADOR!",
                        createdUser.getUsername(),
                        createdUser.getEmail(),
                        createdUser.getId()
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                // Usuarios siguientes: asignar rol USER por defecto
                Role userRole = roleService.getRoleByName("USER")
                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
                userService.assignRoleToUser(createdUser.getId(), userRole.getId());

                AuthResponse response = new AuthResponse(
                        "Usuario registrado exitosamente con rol USER",
                        createdUser.getUsername(),
                        createdUser.getEmail(),
                        createdUser.getId()
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.getUserByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciales inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            if (!user.isEnabled()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuario deshabilitado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Generar JWT Token
            Instant now = Instant.now();
            long expiry = 3600L; // 1 hora
            
            String roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("http://localhost:9000")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(user.getUsername())
                    .claim("scope", "openid profile read write")
                    .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                    .claim("username", user.getUsername())
                    .claim("email", user.getEmail())
                    .claim("userId", user.getId())
                    .build();

            String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            Map<String, Object> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("token_type", "Bearer");
            response.put("expires_in", expiry);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("userId", user.getId());
            response.put("roles", user.getRoles().stream().map(Role::getName).toList());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        boolean exists = userService.getUserByUsername(username).isPresent();
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("exists", exists);
        response.put("available", !exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam String username) {
        try {
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("enabled", user.isEnabled());
            response.put("roles", user.getRoles().stream().map(Role::getName).toList());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getAllUsers().size());
        stats.put("totalRoles", roleService.getAllRoles().size());
        stats.put("hasAdmin", userService.getAllUsers().stream()
                .anyMatch(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("ADMIN"))));
        return ResponseEntity.ok(stats);
    }
}
