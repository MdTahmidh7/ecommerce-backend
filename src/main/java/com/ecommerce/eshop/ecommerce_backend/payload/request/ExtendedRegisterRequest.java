package com.ecommerce.eshop.ecommerce_backend.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExtendedRegisterRequest {

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    private String name;

    private Integer upazilaId;

    private String districtName;

    private String UpazilaName;
}
