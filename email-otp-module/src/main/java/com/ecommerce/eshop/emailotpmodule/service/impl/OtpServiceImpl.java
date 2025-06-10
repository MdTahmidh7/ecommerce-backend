package com.ecommerce.eshop.emailotpmodule.service.impl;

import com.ecommerce.eshop.emailotpmodule.dto.request.OtpRequest;
import com.ecommerce.eshop.emailotpmodule.dto.request.OtpVerificationRequest;
import com.ecommerce.eshop.emailotpmodule.dto.response.OtpResponse;
import com.ecommerce.eshop.emailotpmodule.entity.Otp; // Corrected import
import com.ecommerce.eshop.emailotpmodule.repository.OtpRepository; // Corrected import
import com.ecommerce.eshop.emailotpmodule.service.OtpService; // Corrected import
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    @Value("${otp.validity.minutes:5}")
    private long otpValidityMinutes;

    @Value("${otp.resend.cooldown.seconds:60}")
    private long otpResendCooldownSeconds;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public OtpServiceImpl(OtpRepository otpRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public OtpResponse sendOtp(OtpRequest otpRequest) {

        String email = otpRequest.getEmail();
        LocalDateTime now = LocalDateTime.now();

        // 1. Check for cooldown period (will only consider non-deleted OTPs due to @Where)
        Optional<Otp> latestOtpOptional = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (latestOtpOptional.isPresent()) {
            Otp latestOtp = latestOtpOptional.get();
            Duration duration = Duration.between(latestOtp.getCreatedAt(), now);
            if (duration.getSeconds() < otpResendCooldownSeconds) {
                long remainingCooldown = otpResendCooldownSeconds - duration.getSeconds();
                return new OtpResponse(
                        "Please wait " + remainingCooldown + " seconds before requesting another OTP.",
                        false,
                        remainingCooldown
                );
            }
        }

        // 2. Invalidate any existing *active* OTPs for this email (mark as used, not deleted)
        // This implicitly works with @Where, only finding non-deleted ones.
        otpRepository.findByEmailAndIsUsedFalse(email).ifPresent(otp -> {
            otp.setUsed(true); // Mark as used/invalidated
            otpRepository.save(otp);
        });

        // 3. Generate new OTP
        String otpCode = generateOtpCode(6);
        LocalDateTime expiryTime = now.plusMinutes(otpValidityMinutes);

        // 4. Save OTP to database
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(expiryTime);
        // isUsed and isDeleted are defaulted by @PrePersist and columnDefinition
        otpRepository.save(otp);

        // 5. Send OTP via email
        try {
            sendEmail(email, "Your OTP for E-Shop Verification", "Your One-Time Password (OTP) is: " + otpCode +
                    "\nThis OTP is valid for " + otpValidityMinutes + " minutes. Do not share it with anyone.");
            return new OtpResponse("OTP sent successfully to " + email, true, 0L);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email to " + email + ": " + e.getMessage());
            return new OtpResponse("Failed to send OTP. Please try again.", false, 0L);
        }
    }

    @Override
    @Transactional
    public boolean verifyOtp(OtpVerificationRequest otpVerificationRequest) {
        String email = otpVerificationRequest.getEmail();
        String providedOtp = otpVerificationRequest.getOtpCode();
        LocalDateTime now = LocalDateTime.now();

        // This will only find non-deleted, non-used, non-expired OTPs
        Optional<Otp> otpOptional = otpRepository.findByEmailAndIsUsedFalseAndExpiryTimeAfter(email, now);

        if (otpOptional.isPresent()) {
            Otp otp = otpOptional.get();
            if (otp.getOtpCode().equals(providedOtp)) {
                otp.setUsed(true); // Mark OTP as used
                otpRepository.save(otp);
                // Optionally, if you want to explicitly soft-delete after successful verification:
                // otpRepository.delete(otp); // This would trigger the @SQLDelete
                return true; // OTP is valid and matched
            }
        }
        return false; // OTP not found, expired, used, deleted, or mismatched
    }

    private String generateOtpCode(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
