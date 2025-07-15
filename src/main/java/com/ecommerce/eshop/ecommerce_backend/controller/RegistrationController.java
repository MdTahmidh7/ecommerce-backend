package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.payload.request.ExtendedRegisterRequest;
import com.ecommerce.eshop.ecommerce_backend.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody ExtendedRegisterRequest registerRequest) {
        try {
            registrationService.registerNewUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
