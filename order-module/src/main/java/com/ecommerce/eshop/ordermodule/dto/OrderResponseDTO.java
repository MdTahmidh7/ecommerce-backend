package com.ecommerce.eshop.ordermodule.dto;

import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal totalPrice;
    private Long upazilaId;
    private OrderStatus status;
    private Instant creationDate;
    private Long productId;
    private String productName;
    private List<OrderItemDTO> orderItems;

}
