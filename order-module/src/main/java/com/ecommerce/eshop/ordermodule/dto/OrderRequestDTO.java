package com.ecommerce.eshop.ordermodule.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotEmpty
    private String districtName;

    @NotEmpty
    private String upazilaName;

    private Long upazilaId;

    private Long userId;

    private List<OrderItemDTO> orderItems;
}
