package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ExtendedRegisterRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.VerifyOtpAndRegisterRequest;
import com.ecommerce.eshop.ecommerce_backend.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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


    //create an api for new registration with phone number and send otp to that phone number
    @PostMapping
    public ResponseEntity<String> register(
            @Valid @RequestBody ExtendedRegisterRequest registerRequest
    ) {
        try {
            registrationService.register(registerRequest);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @Valid @RequestBody ExtendedRegisterRequest registerRequest
    ) {
        try {
            registrationService.sendRegistrationOtp(registerRequest);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<JwtResponseDTO> verifyOtpAndRegister(
            @Valid @RequestBody VerifyOtpAndRegisterRequest registerRequest
    ) {
        try {
            JwtResponseDTO jwtResponseDTO = registrationService.verifyOtpAndRegister(registerRequest);
            return ResponseEntity.ok(jwtResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
