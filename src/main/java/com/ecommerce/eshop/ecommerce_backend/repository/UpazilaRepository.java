package com.ecommerce.eshop.ecommerce_backend.repository;


import com.ecommerce.eshop.ecommerce_backend.entity.Upazila;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpazilaRepository extends JpaRepository<Upazila, Integer> {

    Page<Upazila> findByDistrictId(Integer districtId, Pageable pageable);

}

