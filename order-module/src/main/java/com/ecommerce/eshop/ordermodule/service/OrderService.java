package com.ecommerce.eshop.ordermodule.service;

import com.ecommerce.eshop.ordermodule.dto.OrderDetailsDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderRequestDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderResponseDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderSummaryDTO;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, Long userId);

    OrderDetailsDTO updateOrderStatus(
            Long orderId,
            OrderStatus newStatus
    );

    Page<OrderSummaryDTO> getAllOrders(
            OrderStatus status,
            String startDate,
            String endDate,
            Long userId,
            Pageable pageable
    );

    Page<OrderResponseDTO> getAllOrdersForAdmin(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Pageable pageable
    );

    Page<OrderSummaryDTO> getAllOrderSummaries(
            OrderStatus orderStatus,
            String from,
            String to,
            Pageable pageable
    );

    OrderDetailsDTO getOrderDetailsByOrderId(Long orderId);
}
