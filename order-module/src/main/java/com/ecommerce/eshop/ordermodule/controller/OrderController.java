package com.ecommerce.eshop.ordermodule.controller;

import com.ecommerce.eshop.ordermodule.dto.OrderRequestDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderResponseDTO;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import com.ecommerce.eshop.ordermodule.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        // TODO: Get user ID from security context
        Long userId = 1L;
        return ResponseEntity.ok(orderService.createOrder(orderRequestDTO, userId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<OrderResponseDTO> orders = orderService.getAllOrders(status, startDate, endDate);
        return ResponseEntity.ok(orders);
    }
}
