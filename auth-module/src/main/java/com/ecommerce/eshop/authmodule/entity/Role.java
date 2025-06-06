package com.ecommerce.eshop.authmodule.entity;

import com.ecommerce.eshop.authmodule.model.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Store enum name as string
    @Column(length = 20, unique = true, nullable = false)
    private RoleName name; // Use an Enum for better type safety

    private String description;

    public Role(RoleName name) {
        this.name = name;
    }
}
