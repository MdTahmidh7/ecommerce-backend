package com.ecommerce.eshop.ordermodule.service;

import com.ecommerce.eshop.ordermodule.dto.OrderRequestDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderResponseDTO;
import com.ecommerce.eshop.ordermodule.entity.Order;
import com.ecommerce.eshop.ordermodule.entity.OrderItem;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import com.ecommerce.eshop.ordermodule.repository.OrderRepository;
import com.ecommerce.eshop.shippingmodule.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, Long userId) {

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(orderRequestDTO.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDate(Instant.now());

        order.setOrderItems(orderRequestDTO.getOrderItems().stream().map(orderItemDTO -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPrice(orderItemDTO.getPrice());
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setProductId(orderItemDTO.getProductId());
            return orderItem;
        }).collect(Collectors.toList()));

        order.setTotalPrice(order.getOrderItems()
                .stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Order savedOrder = orderRepository.save(order);

        rabbitTemplate.convertAndSend("q.order.created", toOrderCreatedEvent(savedOrder));

        return toDto(savedOrder);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = optionalOrder.get();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return toDto(updatedOrder);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate) {

        List<Order> orders;

        if (status != null && startDate != null && endDate != null) {
            orders = orderRepository.findByStatusAndCreationDateBetween(
                    status,
                    startDate.atStartOfDay(ZoneOffset.UTC).toInstant(),
                    endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant()
            );
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByCreationDateBetween(
                    startDate.atStartOfDay(ZoneOffset.UTC).toInstant(),
                    endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant()
            );
        } else {
            orders = orderRepository.findAll();
        }

        return orders
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private OrderCreatedEvent toOrderCreatedEvent(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getShippingAddress(),
                order.getOrderItems().stream().map(orderItem ->
                        new com.ecommerce.eshop.shippingmodule.dto.OrderItemDTO(
                                orderItem.getProductId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice()
                        )
                ).collect(Collectors.toList())
        );
    }

    private OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setStatus(order.getStatus());
        dto.setCreationDate(order.getCreationDate());
        dto.setOrderItems(order.getOrderItems().stream().map(orderItem -> {
            var orderItemDto = new com.ecommerce.eshop.ordermodule.dto.OrderItemDTO();
            orderItemDto.setPrice(orderItem.getPrice());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setProductId(orderItem.getProductId());
            return orderItemDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
