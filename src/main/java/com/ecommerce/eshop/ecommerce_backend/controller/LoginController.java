package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.ecommerce_backend.payload.request.LoginRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ResendOtpRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.VerifyOtpLoginRequest;
import com.ecommerce.eshop.ecommerce_backend.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;


    @PostMapping
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            loginService.login(loginRequest);
            Map<String, String> successResponse = Map.of("message", "OTP sent successfully.");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<JwtResponseDTO> verifyOtpAndLogin(
            @Valid @RequestBody VerifyOtpLoginRequest verifyOtpLoginRequest
    ) {
        try {
            JwtResponseDTO jwtResponseDTO = loginService.verifyOtpAndLogin(verifyOtpLoginRequest);
            return ResponseEntity.ok(jwtResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody ResendOtpRequest resendOtpRequest) {
        try {
            loginService.resendOtp(resendOtpRequest);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
