package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.entity.PhoneOtp;
import com.ecommerce.eshop.ecommerce_backend.repository.PhoneOtpRepository;
import com.ecommerce.eshop.ecommerce_backend.service.PhoneOtpService;
import com.ecommerce.eshop.ecommerce_backend.service.SmsSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneOtpServiceImpl implements PhoneOtpService {

    private final PhoneOtpRepository phoneOtpRepository;
    private final SmsSender smsSender;

    @Value("${otp.service.enabled}")
    private boolean otpServiceEnabled;

    @Value("${default.otp}")
    private String defaultOtp;


    @Override
    public void sendOtp(String phoneNumber) {
        Optional<PhoneOtp> existingOtp = phoneOtpRepository.findByPhoneNumberAndIsUsedFalse(phoneNumber);

        if (existingOtp.isPresent() && existingOtp.get().getExpiryTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException(
                    "An active OTP already exists. " +
                    "Please wait 5 minutes before requesting a new one."
            );
        }

        existingOtp.ifPresent(phoneOtpRepository::delete);

        String otpCode = otpServiceEnabled ? generateOtp() : defaultOtp;

        PhoneOtp phoneOtp = new PhoneOtp();
        phoneOtp.setPhoneNumber(phoneNumber);
        phoneOtp.setOtpCode(otpCode);
        phoneOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));


        if (otpServiceEnabled) {
            boolean sent = smsSender.sendSms(phoneNumber, "Your shaheensanitary.com OTP is: " + otpCode + " (valid for 5 minutes)");
            if (sent){
                phoneOtpRepository.save(phoneOtp);
                log.info("OTP SMS sent to {}: {}", phoneNumber, otpCode);
                return;
            }
            log.error("Failed to send OTP SMS to {}", phoneNumber);
            throw new RuntimeException("Failed to send OTP SMS. Please try again later.");
        } else {
            log.info("OTP service disabled. Using default OTP for phone number {}: {}", phoneNumber, otpCode);
        }
    }

    @Override
    public boolean verifyOtp(
            String phoneNumber,
            String otp
    ) {

        PhoneOtp phoneOtp = phoneOtpRepository
                .findByPhoneNumberAndIsUsedFalse(phoneNumber)
                .orElse(null);

        if (phoneOtp == null) {
            return false;
        }

        if (phoneOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!phoneOtp.getOtpCode().equals(otp)) {
            return false;
        }

        phoneOtp.setUsed(true);
        phoneOtpRepository.save(phoneOtp);

        return true;
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
