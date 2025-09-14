package com.ecommerce.eshop.ecommerce_backend.repository;

import com.ecommerce.eshop.ecommerce_backend.entity.PhoneOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneOtpRepository extends JpaRepository<PhoneOtp, Long> {

    @Query("""
    SELECT p FROM PhoneOtp p
    WHERE p.phoneNumber = :phoneNumber
    AND p.isUsed = false
    """)
    Optional<PhoneOtp> findByPhoneNumberAndIsUsedFalse(String phoneNumber);
}
