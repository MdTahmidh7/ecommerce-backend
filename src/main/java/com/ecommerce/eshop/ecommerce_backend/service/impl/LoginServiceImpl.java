package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.repository.UserRepository;
import com.ecommerce.eshop.authmodule.service.JwtService;
import com.ecommerce.eshop.ecommerce_backend.payload.request.LoginRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ResendOtpRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.VerifyOtpLoginRequest;
import com.ecommerce.eshop.ecommerce_backend.service.LoginService;
import com.ecommerce.eshop.ecommerce_backend.service.PhoneOtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PhoneOtpService phoneOtpService;
    private final JwtService jwtService;

    @Override
    public void login(LoginRequest loginRequest) {

        if (!userRepository.existsByPhoneNumber(loginRequest.getPhoneNumber())) {
            throw new RuntimeException("User not found with this phone number.");
        }
        phoneOtpService.sendOtp(loginRequest.getPhoneNumber());
        log.info("LoginServiceImpl -> OTP sent to {}", loginRequest.getPhoneNumber());
    }

    @Override
    public JwtResponseDTO verifyOtpAndLogin(VerifyOtpLoginRequest verifyOtpLoginRequest) {

        if (
                !phoneOtpService.verifyOtp(verifyOtpLoginRequest.getPhoneNumber(),
                verifyOtpLoginRequest.getOtp())
        ) {
            log.error(
                    "LoginServiceImpl -> Invalid or expired OTP for {}",
                    verifyOtpLoginRequest.getPhoneNumber()
            );
            throw new RuntimeException("Invalid or expired OTP.");
        }

        User user = userRepository
                .findByPhoneNumber(verifyOtpLoginRequest.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found with this phone number."));

        String jwt = jwtService.generateToken(user);

        List<String> roles = user.getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        return new JwtResponseDTO(
                jwt,
                user.getId(),
                user.getPhoneNumber(),
                roles,
                user.getFirstName(),
                user.getLastName(),
                user.getAddress()
        );
    }

    @Override
    public void resendOtp(ResendOtpRequest resendOtpRequest) {

        if (!userRepository.existsByPhoneNumber(resendOtpRequest.getPhoneNumber())) {
            throw new RuntimeException("User not found with this phone number.");
        }
        phoneOtpService.sendOtp(resendOtpRequest.getPhoneNumber());
        log.info("LoginServiceImpl -> OTP resent to {}", resendOtpRequest.getPhoneNumber());
    }
}
