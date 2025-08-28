package com.ecommerce.eshop.ordermodule.repository;

import com.ecommerce.eshop.ordermodule.dto.OrderSummaryDTO;
import com.ecommerce.eshop.ordermodule.entity.Order;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
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
    Page<Order> findOrdersByFilters(
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    // Lightweight query for list view
    @Query(value = """
                SELECT o.id                                   as order_id,
                       CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                       u.phone_number                         as customer_phone_number,
                       o.total_price                          as total_price,
                       o.status                               as status,
                       cast(o.creation_date as timestamp)     as creation_date,
                       oi.quantity                            AS item_count,
                       oi.product_id                          as product_id
                FROM orders o
                         JOIN users u ON o.user_id = u.id
                         LEFT JOIN order_items oi ON oi.order_id = o.id
                WHERE
                    o.status = COALESCE(:status, o.status)
                    AND o.creation_date >= COALESCE(:from, o.creation_date)
                    AND o.creation_date <= COALESCE(:to, o.creation_date)
                ORDER BY o.creation_date DESC;
            """, nativeQuery = true)
    List<Object[]> findAllOrderSummariesNative(
            @Param("status") String status,
            @Param("from") Timestamp from,
            @Param("to") Timestamp to
    );

}
