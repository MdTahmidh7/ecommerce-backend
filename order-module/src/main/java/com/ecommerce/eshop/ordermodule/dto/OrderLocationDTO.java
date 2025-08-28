package com.ecommerce.eshop.ordermodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderLocationDTO {

    // Upazila fields
    private Long upazilaId;
    private String upazilaName;
    private String upazilaBnName;

    // District fields
    private Long districtId;
    private String districtName;
    private String districtBnName;

    // Division fields
    private Long divisionId;
    private String divisionName;
    private String divisionBnName;

}
