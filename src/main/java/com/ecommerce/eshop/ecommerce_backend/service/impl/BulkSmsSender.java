package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.service.SmsSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
//@Profile("prod")
public class BulkSmsSender implements SmsSender {

    @Value("${bulksms.api-key}")
    private String apiKey;

    @Value("${bulksms.sender-id}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean sendSms(String to, String message) {
        String apiUrl = "http://bulksmsbd.net/api/smsapi";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("api_key", apiKey);
        params.add("senderid", senderId);
        params.add("number", to);
        params.add("message", message);
        params.add("type", "text");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            String response = restTemplate.postForObject(apiUrl, request, String.class);

            JsonNode json = objectMapper.readTree(response);
            int responseCode = json.get("response_code").asInt();

            if (responseCode == 202) {
                System.out.println("SMS sent to " + to);
                return true;
            } else {
                System.err.println("SMS failed: " + json.get("error_message").asText());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
            return false;
        }
    }
}


