package com.ecommerce.eshop.ordermodule.service;


import com.ecommerce.eshop.authmodule.entity.User;
import com.ecommerce.eshop.authmodule.repository.UserRepository;
import com.ecommerce.eshop.ordermodule.dto.*;
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
        order.setDistrictName(orderRequestDTO.getDistrictName());
        order.setUpazilaName(orderRequestDTO.getUpazilaName());
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
    public OrderDetailsDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = optionalOrder.get();
        order.setStatus(newStatus);
        orderRepository.save(order);

        return getOrderDetailsByOrderId(orderId);
    }

    @Override
    public Page<OrderSummaryDTO> getAllOrders(
            OrderStatus orderStatus,
            String from,
            String to,
            Long userId,
            Pageable pageable
    ) {
        Timestamp fromTs = null;
        Timestamp toTs = null;

        if (from != null && to != null) {
            fromTs = Timestamp.valueOf(from + " 00:00:00");
            toTs = Timestamp.valueOf(to + " 23:59:59");
        }

        String orderStatusStr = orderStatus != null ? orderStatus.name() : null;

        Page<Object[]> results = orderRepository.findAllOrderSummariesForUserNative(
                userId,
                orderStatusStr,
                fromTs,
                toTs,
                pageable
        );

        // Correctly map the Page<Object[]> to a Page<OrderSummaryDTO>
        return results.map(row -> {
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
            // Use Number to safely handle different numeric types
            Long productId = row[7] != null ? ((Number) row[7]).longValue() : null;

            return new OrderSummaryDTO(
                    orderId,
                    customerName,
                    customerPhone,
                    null,
                    status,
                    productId,
                    null,
                    totalItems,
                    totalPrice,
                    creationDate
            );
        });
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
                        user -> user.getName()
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
    public Page<OrderSummaryDTO> getAllOrderSummaries(
            OrderStatus orderStatus,
            String from,
            String to,
            Pageable pageable
    ) {
        Timestamp fromTs = Timestamp.valueOf(from + " 00:00:00");
        Timestamp toTs = Timestamp.valueOf(to + " 23:59:59");

        String orderStatusStr = orderStatus != null ? orderStatus.name() : null;

        Page<Object[]> results = orderRepository.findAllOrderSummariesNative(
                orderStatusStr,
                fromTs,
                toTs,
                pageable
        );

        // Correctly map the Page<Object[]> to a Page<OrderSummaryDTO>
        return results.map(row -> {
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
            Long productId = row[7] != null ? ((Number) row[7]).longValue() : null;

            return new OrderSummaryDTO(
                    orderId,
                    customerName,
                    customerPhone,
                    null,
                    status,
                    productId,
                    null,
                    totalItems,
                    totalPrice,
                    creationDate
            );
        });
    }

    @Override
    public OrderDetailsDTO getOrderDetailsByOrderId(Long orderId) {

        Object[] row = (Object[]) orderRepository.findOrderDetailsByOrderId(orderId);

        if (row == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        OrderSummaryDTO orderSummary = new OrderSummaryDTO(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).longValue(),
                OrderStatus.valueOf((String) row[4]),
                ((Number) row[5]).longValue(),
                ((Number) row[6]).doubleValue(),
                ((Number) row[7]).intValue(),
                (BigDecimal) row[8],
                ((Timestamp) row[9]).toInstant()
        );

        Object[] orderLocationObject = (Object[]) orderRepository
                .getOrderLocationByUpazilaId(orderSummary.getUpazilaId());

        Object[] productDetails = (Object[]) orderRepository
                .getProductDetailsByProductId(orderSummary.getProductId());

        OrderLocationDTO orderLocationDTO = new OrderLocationDTO(
                ((Number) orderLocationObject[6]).longValue(),
                (String) orderLocationObject[0],
                (String) orderLocationObject[3],
                ((Number) orderLocationObject[7]).longValue(),
                (String) orderLocationObject[1],
                (String) orderLocationObject[4],
                ((Number) orderLocationObject[8]).longValue(),
                (String) orderLocationObject[2],
                (String) orderLocationObject[5]
        );

        String productName = productDetails != null ? (String) productDetails[1] : "N/A";
        String productImage = productDetails != null ? (String) productDetails[2] : "N/A";

        //prepare OrderDetailsDTO
        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();

        orderDetailsDTO.setOrderId(orderSummary.getOrderId());
        orderDetailsDTO.setCustomerName(orderSummary.getCustomerName());
        orderDetailsDTO.setCustomerPhone(orderSummary.getCustomerPhone());
        orderDetailsDTO.setUpazilaId(orderSummary.getUpazilaId());
        orderDetailsDTO.setStatus(orderSummary.getStatus());
        orderDetailsDTO.setProductId(orderSummary.getProductId());
        orderDetailsDTO.setProductPrice(orderSummary.getProductPrice());
        orderDetailsDTO.setProductCount(orderSummary.getProductCount());
        orderDetailsDTO.setTotalPrice(orderSummary.getTotalPrice());
        orderDetailsDTO.setCreationDate(orderSummary.getCreationDate());
        orderDetailsDTO.setProductName(productName);
        orderDetailsDTO.setImageUrl(productImage);
        orderDetailsDTO.setUpazilaName(orderLocationDTO.getUpazilaName());
        orderDetailsDTO.setDistrictName(orderLocationDTO.getDistrictName());
        orderDetailsDTO.setDivisionName(orderLocationDTO.getDivisionName());
        orderDetailsDTO.setAddress(" ");

        return orderDetailsDTO;
    }
}
