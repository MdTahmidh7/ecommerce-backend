package com.ecommerce.eshop.emailotpmodule.repository; // Corrected package name

import com.ecommerce.eshop.emailotpmodule.entity.Otp; // Corrected import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    // Note: Due to @Where(clause = "is_deleted = false"), all these methods will
    // implicitly only find OTPs where is_deleted is false.

    Optional<Otp> findByEmailAndIsUsedFalseAndExpiryTimeAfter(
            String email,
            LocalDateTime currentTime);

    // This will find the latest non-deleted OTP for cooldown checks
    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);

    // This will find any currently active (non-used, non-deleted) OTP for an email
    Optional<Otp> findByEmailAndIsUsedFalse(String email);

    // This will soft-delete OTPs by email. The @SQLDelete on the entity will be used.
    // Spring Data JPA's delete methods will trigger the @SQLDelete.
    // Consider if you need a specific method to find *all* (including soft-deleted) for auditing.
    // If so, you'd need a custom query or use a special Spring Data JPA method (e.g., using @Query)
    // or by accessing the entity manager directly for non-@Where filtered access.
    // For cleanup, you might want to physically delete truly old records, not just soft delete.
    // For now, deleteById will perform soft delete.
    // For physical deletion, you'd need a custom query with @Modifying.
}
