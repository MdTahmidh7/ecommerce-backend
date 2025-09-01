package com.ecommerce.eshop.ecommerce_backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VerifyOtpAndRegisterRequest extends ExtendedRegisterRequest {

    @NotBlank(message = "OTP is required")
    private String otp;
}
