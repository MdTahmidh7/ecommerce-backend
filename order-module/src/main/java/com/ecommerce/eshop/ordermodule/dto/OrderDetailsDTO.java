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
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsDTO {

    private Long orderId;
    private String customerName;
    private String customerPhone;
    private Long upazilaId;
    private OrderStatus status;
    private Long productId;
    private Double productPrice;
    private Integer productCount;
    private BigDecimal totalPrice;
    private Instant creationDate;
    private String productName;
    private String imageUrl;
    private String upazilaName;
    private String divisionName;
    private String districtName;
    private String address;

}
