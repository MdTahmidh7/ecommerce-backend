package com.ecommerce.eshop.emailotpmodule.entity; // Corrected package name as per your prompt

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// Import these for soft delete
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "otps", uniqueConstraints = {
        // This constraint is tricky with soft delete if you need unique active emails.
        // Consider if you need a unique constraint on (email, is_deleted) or just email.
        // For OTPs, it's often better to just prevent multiple *active* OTPs for an email.
        // The previous unique constraint on 'email' alone might conflict with soft delete,
        // as it would prevent a new OTP for a 'soft-deleted' email record.
        // Let's remove the @UniqueConstraint from @Table and manage active uniqueness in service.
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE otps SET is_deleted = true WHERE id=?") // <--- SQL for soft delete
@Where(clause = "is_deleted = false") // <--- Only retrieve non-deleted records by default
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Removed unique constraint here; service will handle active OTP uniqueness
    private String email;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isUsed;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted; // <--- NEW FIELD for soft delete

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
        this.isDeleted = false; // <--- Default to false on creation
    }
}
