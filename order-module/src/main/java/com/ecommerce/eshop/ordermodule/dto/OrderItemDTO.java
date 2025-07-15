package com.ecommerce.eshop.ordermodule.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
