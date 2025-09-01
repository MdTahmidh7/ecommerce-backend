package com.ecommerce.eshop.ecommerce_backend.repository;

import com.ecommerce.eshop.ecommerce_backend.entity.PhoneOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneOtpRepository extends JpaRepository<PhoneOtp, Long> {

    Optional<PhoneOtp> findByPhoneNumberAndIsUsedFalse(String phoneNumber);
}
