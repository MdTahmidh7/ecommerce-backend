package com.ecommerce.eshop.emailotpmodule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {

    private String message;

    private boolean success;

    private Long remainingCooldownSeconds; // For resend logic

}
