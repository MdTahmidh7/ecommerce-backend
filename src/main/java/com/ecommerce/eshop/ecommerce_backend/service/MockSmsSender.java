package com.ecommerce.eshop.ecommerce_backend.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("docker")  // This bean only loads in local environment
public class MockSmsSender implements SmsSender {

    @Override
    public boolean sendSms(String to, String message) {
        System.out.println("📩 MOCK SMS to " + to + ": " + message);
        return true; // always succeed in dev
    }
}
