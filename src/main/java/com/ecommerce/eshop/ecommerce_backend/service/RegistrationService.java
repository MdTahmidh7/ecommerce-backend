package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.authmodule.dto.RegisterRequestDTO;
import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.repository.UserRepository;
import com.ecommerce.eshop.authmodule.service.AuthService;
import com.ecommerce.eshop.authmodule.service.JwtService;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ExtendedRegisterRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.VerifyOtpAndRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AuthService authService;
    private final UserProfileService userProfileService;
    private final PhoneOtpService phoneOtpService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public void sendRegistrationOtp(ExtendedRegisterRequest registerRequest) {

        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use!");
        }
        phoneOtpService.sendOtp(registerRequest.getPhoneNumber());
    }

    @Transactional
    public JwtResponseDTO verifyOtpAndRegister(VerifyOtpAndRegisterRequest extendedRequest) {

        if (!phoneOtpService.verifyOtp(extendedRequest.getPhoneNumber(), extendedRequest.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP.");
        }

        // 1. Create the DTO for the auth-module
        RegisterRequestDTO authRequest = new RegisterRequestDTO();
        authRequest.setPhoneNumber(extendedRequest.getPhoneNumber());
        authRequest.setFirstName(extendedRequest.getFirstName());
        authRequest.setLastName(extendedRequest.getLastName());
        //authRequest.setAddress(extendedRequest.getAddress());
        authRequest.setPassword(UUID.randomUUID().toString()); // Generate a random password

        // 2. Call the AuthService directly to register the user
        User newUser = authService.registerUser(authRequest);

        // 3. Create the user profile in the e-commerce backend
        userProfileService.createOrUpdateProfile(newUser.getId(), extendedRequest.getUpazilaId());

        // 4. Generate JWT token
        String jwt = jwtService.generateToken(newUser);

        List<String> roles = newUser.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        return new JwtResponseDTO(jwt, newUser.getId(), newUser.getPhoneNumber(), roles,
                newUser.getFirstName(), newUser.getLastName(),
                newUser.getAddress());
    }

    public void register(ExtendedRegisterRequest extendedRequest) {

        if (userRepository.existsByPhoneNumber(extendedRequest.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already in use!");
        }

        //register user and save
        RegisterRequestDTO authRequest = new RegisterRequestDTO();
        authRequest.setPhoneNumber(extendedRequest.getPhoneNumber());
        authRequest.setFirstName(extendedRequest.getFirstName());
        authRequest.setLastName(extendedRequest.getLastName());
        //authRequest.setAddress(extendedRequest.getAddress());
        authRequest.setPassword(UUID.randomUUID().toString()); // Generate a random password
        authRequest.setDistrictName(extendedRequest.getDistrictName());
        authRequest.setUpazilaName(extendedRequest.getUpazilaName());

        // 2. Call the AuthService directly to register the user
        User newUser = authService.registerUser(authRequest);

        // 3. Create the user profile in the e-commerce backend
        userProfileService.createOrUpdateProfile(newUser.getId(), extendedRequest.getUpazilaId());

        // 4. Generate JWT token
        //String jwt = jwtService.generateToken(newUser);

//        List<String> roles = newUser
//                .getAuthorities()
//                .stream()
//                .map(Object::toString)
//                .toList();


        phoneOtpService.sendOtp(extendedRequest.getPhoneNumber());

    }
}
