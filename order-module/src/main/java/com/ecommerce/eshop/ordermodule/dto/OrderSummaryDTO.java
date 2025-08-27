package com.ecommerce.eshop.ordermodule.dto;

import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Long orderId;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Instant creationDate;

    // Just basic item info for the list view
    private Integer totalItems;
    private Long productId; // First/main product name
}
