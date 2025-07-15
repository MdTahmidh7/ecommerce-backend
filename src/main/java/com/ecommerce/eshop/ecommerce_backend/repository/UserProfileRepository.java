package com.ecommerce.eshop.ecommerce_backend.repository;

import com.ecommerce.eshop.ecommerce_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
