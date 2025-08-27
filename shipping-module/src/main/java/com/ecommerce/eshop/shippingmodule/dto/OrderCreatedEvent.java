package com.ecommerce.eshop.shippingmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private Long upazilaId;
    private List<OrderItemDTO> orderItems;
}
