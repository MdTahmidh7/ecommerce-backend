package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.authmodule.dto.JwtResponseDTO;
import com.ecommerce.eshop.ecommerce_backend.payload.request.LoginRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ResendOtpRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.request.VerifyOtpLoginRequest;

public interface LoginService {

    void login(LoginRequest loginRequest);

    JwtResponseDTO verifyOtpAndLogin(VerifyOtpLoginRequest verifyOtpLoginRequest);

    void resendOtp(ResendOtpRequest resendOtpRequest);

}
