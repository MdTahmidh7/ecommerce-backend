package com.ecommerce.eshop.ordermodule.repository;

import com.ecommerce.eshop.ordermodule.entity.Order;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCreationDateBetween(Instant startDate, Instant endDate);
    List<Order> findByStatusAndCreationDateBetween(OrderStatus status, Instant startDate, Instant endDate);
    List<Order> findByUserId(Long userId);

    @Query(value = """
            SELECT o.* FROM orders o
            JOIN order_items oi ON o.id = oi.order_id
            WHERE (cast(:status as text) IS NULL OR o.status = :status)
              AND (cast(:startDate as timestamp) IS NULL OR o.creation_date >= cast(:startDate as timestamp))
              AND (cast(:endDate as timestamp) IS NULL OR o.creation_date <= cast(:endDate as timestamp))
              AND (:categoryId IS NULL OR oi.product_id IN (SELECT p.id FROM products p WHERE p.category_id = :categoryId))
            """,
            nativeQuery = true)
    List<Order> findOrdersByFilters(
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("categoryId") Long categoryId
    );
}
