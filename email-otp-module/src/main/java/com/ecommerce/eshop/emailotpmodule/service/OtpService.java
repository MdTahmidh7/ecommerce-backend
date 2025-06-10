package com.ecommerce.eshop.emailotpmodule.service;


import com.ecommerce.eshop.emailotpmodule.dto.request.OtpRequest;
import com.ecommerce.eshop.emailotpmodule.dto.request.OtpVerificationRequest;
import com.ecommerce.eshop.emailotpmodule.dto.response.OtpResponse;

public interface OtpService {
    /**
     * Generates an OTP, saves it, and sends it to the specified email address.
     * Also handles cooldown logic to prevent spamming OTP requests.
     *
     * @param otpRequest Contains the email address to send the OTP to.
     * @return An OtpResponse indicating success/failure and potential cooldown time.
     */
    OtpResponse sendOtp(OtpRequest otpRequest);

    /**
     * Verifies if the provided OTP code matches the active, non-expired OTP for the given email.
     * If successful, the OTP is marked as used.
     *
     * @param otpVerificationRequest Contains the email and the OTP code to verify.
     * @return true if the OTP is valid and matched, false otherwise.
     */
    boolean verifyOtp(OtpVerificationRequest otpVerificationRequest);
}
