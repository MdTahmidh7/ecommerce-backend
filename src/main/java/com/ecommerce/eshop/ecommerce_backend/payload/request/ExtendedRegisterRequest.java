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

    private String firstName;
    private String lastName;

    @NotBlank
    @Size(max = 500)
    private String address;

    @NotNull(message = "Upazila ID is required")
    private Integer upazilaId;
}
