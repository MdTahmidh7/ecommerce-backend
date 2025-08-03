package com.ecommerce.eshop.authmodule.controller;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.authmodule.dto.LoginRequestDTO;
import com.ecommerce.eshop.authmodule.dto.RegisterRequestDTO;
import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) { // Changed param type and return type
        JwtResponseDTO jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequestDTO registerRequest
    ) { // Changed param type
        try {
            User newUser = authService.registerUser(registerRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully! User ID: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
