package com.ecommerce.eshop.ecommerce_backend.repository;

import com.ecommerce.eshop.ecommerce_backend.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Integer> {}
