package com.ecommerce.eshop.ecommerce_backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExtendedRegisterRequest {

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String firstName;
    private String lastName;

    @NotBlank
    @Size(max = 500)
    private String address;

    @NotNull(message = "Upazila ID is required")
    private Integer upazilaId;
}
