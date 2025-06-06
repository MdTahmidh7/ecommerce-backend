package com.ecommerce.eshop.ecommerce_backend.repository;

import com.ecommerce.eshop.ecommerce_backend.entity.Product;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    Page<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    Page<Product> findByCategoryNameAndIsDeletedFalse(String categoryName, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(String keyword, Pageable pageable);

}
