package com.ecommerce.eshop.ecommerce_backend.service;

public interface SmsSender {

    boolean sendSms(String to, String message);

}
