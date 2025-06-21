package com.ecommerce.eshop.ecommerce_backend.repository;


import com.ecommerce.eshop.ecommerce_backend.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DistrictRepository extends JpaRepository<District, Integer> {

    Page<District> findByDivisionId(Integer divisionId, Pageable pageable);

}

