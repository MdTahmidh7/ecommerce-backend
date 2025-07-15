package com.ecommerce.eshop.ordermodule.repository;

import com.ecommerce.eshop.ordermodule.entity.Order;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCreationDateBetween(Instant startDate, Instant endDate);
    List<Order> findByStatusAndCreationDateBetween(OrderStatus status, Instant startDate, Instant endDate);
}
