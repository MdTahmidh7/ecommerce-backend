package com.ecommerce.eshop.ordermodule.controller;

import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.ordermodule.dto.OrderRequestDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderResponseDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderSummaryDTO;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import com.ecommerce.eshop.ordermodule.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody OrderRequestDTO orderRequestDTO
    ) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderService.createOrder(orderRequestDTO, user.getId()));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersForUser(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<OrderResponseDTO> orders = orderService.getAllOrders(
                status,
                startDate,
                endDate,
                user.getId()
        );

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrdersForAdmin(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable
    ) {
        //Set Default values for pagination page = 0 and size = 10
        if (pageable.getPageNumber() < 0) {
            pageable = Pageable.ofSize(10);
        }
        Page<OrderResponseDTO> orders = orderService.getAllOrdersForAdmin(
                status,
                startDate,
                endDate,
                categoryId,
                pageable
        );
        return ResponseEntity.ok(orders);
    }

    // Returns lightweight summaries
    @GetMapping("/all")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrders() {

        return ResponseEntity.ok(orderService.getAllOrderSummaries());
    }
}
