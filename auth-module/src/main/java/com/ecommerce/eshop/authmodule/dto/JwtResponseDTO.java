package com.ecommerce.eshop.authmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String phoneNumber;
    private List<String> roles;
    private String firstName;
    private String lastName;
    private String address;

    public JwtResponseDTO(String accessToken,
                          Long id,
                          String phoneNumber,
                          List<String> roles,
                          String firstName,
                          String lastName,
                          String address
    ) {
        this.token = accessToken;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }
}
