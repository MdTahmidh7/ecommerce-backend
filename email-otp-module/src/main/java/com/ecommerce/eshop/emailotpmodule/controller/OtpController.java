package com.ecommerce.eshop.emailotpmodule.controller;

import com.ecommerce.eshop.emailotpmodule.dto.request.OtpRequest;
import com.ecommerce.eshop.emailotpmodule.dto.request.OtpVerificationRequest;
import com.ecommerce.eshop.emailotpmodule.dto.response.OtpResponse;
import com.ecommerce.eshop.emailotpmodule.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Endpoint to request sending an OTP to the specified email.
     * Accessible by unauthenticated users.
     *
     * @param otpRequest Contains the email address to send the OTP to.
     * @return ResponseEntity with OtpResponse and appropriate HTTP status.
     */
    @PostMapping("/send")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpRequest otpRequest) {

        OtpResponse response = otpService.sendOtp(otpRequest);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // If the message contains "wait", it indicates a cooldown, return 429 Too Many Requests.
            // Otherwise, it's a general bad request (e.g., failed to send email).
            HttpStatus status = (response.getMessage().contains("wait")) ? HttpStatus.TOO_MANY_REQUESTS : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(response, status);
        }
    }

    /**
     * Endpoint to verify an OTP.
     * Accessible by unauthenticated users.
     *
     * @param verificationRequest Contains the email and the OTP code to verify.
     * @return ResponseEntity with a string message indicating success or failure.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpVerificationRequest verificationRequest) {
        boolean isValid = otpService.verifyOtp(verificationRequest);
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }
}
