package com.ecommerce.eshop.ordermodule.service;


import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.repository.UserRepository;
import com.ecommerce.eshop.ordermodule.dto.OrderRequestDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderResponseDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderSummaryDTO;
import com.ecommerce.eshop.ordermodule.entity.Order;
import com.ecommerce.eshop.ordermodule.entity.OrderItem;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import com.ecommerce.eshop.ordermodule.repository.OrderRepository;
import com.ecommerce.eshop.shippingmodule.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, Long userId) {

        Order order = new Order();
        order.setUserId(userId);
        order.setUpazilaId(orderRequestDTO.getUpazilaId());
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

       // rabbitTemplate.convertAndSend("q.order.created", toOrderCreatedEvent(savedOrder));

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
    public List<OrderResponseDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponseDTO> getAllOrdersForAdmin(
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Pageable pageable
    ) {

        Page<Order> orders = orderRepository.findOrdersByFilters(
                status != null ? status.name() : null,
                startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null,
                endDate != null ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant() : null,
                categoryId,
                pageable
        );

        List<Long> userIdList = orders
                .getContent()
                .stream()
                .map(Order::getUserId)
                .distinct()
                .toList();

        List<User> users = userRepository.findAllById(userIdList);

        Map<Long, String> userIdToNameMap = users
                .stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user.getFirstName() + " " + user.getLastName()
                ));
        //add userName from userIdToNameMap


        List<OrderResponseDTO> orderDtoList = orders
                .getContent()
                .stream()
                .map(order -> {
                    OrderResponseDTO dto = this.toDto(order);
                    String userName = userIdToNameMap.get(dto.getUserId());
                    dto.setUserName(userName);
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(orderDtoList, pageable, orders.getTotalElements());
    }

    private OrderCreatedEvent toOrderCreatedEvent(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getUpazilaId(),
                order.getOrderItems().stream().map(
                        orderItem -> new com.ecommerce.eshop.shippingmodule.dto.OrderItemDTO(
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
        dto.setUpazilaId(order.getUpazilaId());
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

    // Lightweight method for list view
    public List<OrderSummaryDTO> getAllOrderSummaries() {

        List<Object[]> results = orderRepository.findAllOrderSummariesNative();

        return results.stream()
                .map(row -> {
                    Long orderId = ((Number) row[0]).longValue();
                    String customerName = (String) row[1];
                    String customerPhone = (String) row[2];
                    BigDecimal totalPrice = (BigDecimal) row[3];
                    OrderStatus status = OrderStatus.valueOf((String) row[4]);

                    // Handle Instant/Timestamp
                    Instant creationDate;
                    Object creationVal = row[5];
                    if (creationVal instanceof Timestamp ts) {
                        creationDate = ts.toInstant();
                    } else if (creationVal instanceof Instant inst) {
                        creationDate = inst;
                    } else {
                        throw new IllegalStateException("Unexpected type for creationDate: " + creationVal);
                    }

                    Integer totalItems = ((Number) row[6]).intValue();
                    Long productId = row[7] != null ? (Long)row[7] : null;

                    return new OrderSummaryDTO(
                            orderId,
                            customerName,
                            customerPhone,
                            totalPrice,
                            status,
                            creationDate,
                            totalItems,
                            productId
                    );
                })
                .toList();
    }
}
