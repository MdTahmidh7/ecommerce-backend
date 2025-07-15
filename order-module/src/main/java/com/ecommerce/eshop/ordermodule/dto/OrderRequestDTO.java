package com.ecommerce.eshop.ordermodule.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    private String shippingAddress;
    private List<OrderItemDTO> orderItems;
}
