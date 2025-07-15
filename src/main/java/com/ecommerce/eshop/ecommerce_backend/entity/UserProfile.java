package com.ecommerce.eshop.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {

    @Id
    private Long id; // This ID matches the User ID from the auth-module

    @Column(name = "upazila_id")
    private Integer upazilaId;

    // You can add other e-commerce specific fields here in the future,
    // such as shipping addresses, contact preferences, etc.
}
