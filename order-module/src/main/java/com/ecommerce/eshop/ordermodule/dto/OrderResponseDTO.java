package com.ecommerce.eshop.ordermodule.dto;

import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private OrderStatus status;
    private List<OrderItemDTO> orderItems;
}
