package com.ecommerce.eshop.ecommerce_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataSourcePropertiesLogger {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword; // Be cautious with logging passwords in production!

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;

    @PostConstruct
    public void logDataSourceProperties() {
        System.out.println("\n--- DEBUG: Loaded DataSource Properties ---");
        System.out.println("URL: " + dbUrl);
        System.out.println("Username: " + dbUsername);
        System.out.println("Password: " + (dbPassword != null ? "********" : "null")); // Mask password for safety
        System.out.println("Driver Class Name: " + dbDriverClassName);
        System.out.println("-------------------------------------------\n");
    }
}
