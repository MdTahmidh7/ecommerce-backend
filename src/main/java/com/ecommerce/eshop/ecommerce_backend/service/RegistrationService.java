package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.authmodule.dto.RegisterRequestDTO;
import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.service.AuthService;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ExtendedRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AuthService authService;
    private final UserProfileService userProfileService;

    @Transactional
    public void registerNewUser(ExtendedRegisterRequest extendedRequest) {
        // 1. Create the DTO for the auth-module
        RegisterRequestDTO authRequest = new RegisterRequestDTO();
        authRequest.setPhoneNumber(extendedRequest.getPhoneNumber());
        authRequest.setPassword(extendedRequest.getPassword());
        authRequest.setFirstName(extendedRequest.getFirstName());
        authRequest.setLastName(extendedRequest.getLastName());
        authRequest.setAddress(extendedRequest.getAddress());

        // 2. Call the AuthService directly to register the user
        User newUser = authService.registerUser(authRequest);

        // 3. Create the user profile in the e-commerce backend
        userProfileService.createOrUpdateProfile(newUser.getId(), extendedRequest.getUpazilaId());
    }
}
