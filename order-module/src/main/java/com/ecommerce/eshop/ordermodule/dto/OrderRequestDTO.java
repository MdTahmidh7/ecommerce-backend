package com.ecommerce.eshop.ordermodule.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    private Long upazilaId;
    private Long userId;
    private List<OrderItemDTO> orderItems;
}
