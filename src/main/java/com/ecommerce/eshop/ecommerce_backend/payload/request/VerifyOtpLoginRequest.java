package com.ecommerce.eshop.ecommerce_backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VerifyOtpLoginRequest extends LoginRequest {

    @NotBlank(message = "OTP is required")
    private String otp;
}
