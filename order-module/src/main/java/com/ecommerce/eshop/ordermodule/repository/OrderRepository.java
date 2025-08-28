package com.ecommerce.eshop.ordermodule.repository;

import com.ecommerce.eshop.ordermodule.dto.OrderLocationDTO;
import com.ecommerce.eshop.ordermodule.dto.OrderSummaryDTO;
import com.ecommerce.eshop.ordermodule.entity.Order;
import com.ecommerce.eshop.ordermodule.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query(value = """
            SELECT o.id                                   as order_id,
                   CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                   u.phone_number                         as customer_phone_number,
                   o.upazila_id                           as upazila_id,
                   o.status                               as status,
                   oi.product_id                          as product_id,
                   oi.price                               as product_price,
                   oi.quantity                            AS product_count,
                   o.total_price                          as total_price,
                   cast(o.creation_date as timestamp)     as creation_date
            FROM orders o
                     JOIN users u ON o.user_id = u.id
                     LEFT JOIN order_items oi ON oi.order_id = o.id
            WHERE
                o.id = :orderId
            """, nativeQuery = true)
    Object findOrderDetailsByOrderId(
            @Param("orderId") Long orderId
    );

    @Query(value = """
            select upazilas.name     as upazilaName,
                   districts.name    as districtName,
                   divisions.name    as divisionName,
                   upazilas.bn_name  as upazilaBnName,
                   districts.bn_name as districtBnName,
                   divisions.bn_name as divisionBnName,
                   upazilas.id       as upazilaId,
                   districts.id      as districtId,
                   divisions.id      as divisionId
            from upazilas
                     join districts on upazilas.district_id = districts.id
                     join divisions on districts.division_id = divisions.id
            where upazilas.id = :upazilaId
            """, nativeQuery = true)
    Object getOrderLocationByUpazilaId(
            @Param("upazilaId") Long upazilaId
    );


    @Query(value = """
            select p.id          as productId,
                   p.name        as productName,
                   p.image_url   as productImageUrl,
                   p.price       as productPrice,
                   p.stock_quantity as productStockQuantity
            from products p
            where p.id = :productId
            """, nativeQuery = true)
    Object getProductDetailsByProductId(
            @Param("productId") Long productId
    );


}
