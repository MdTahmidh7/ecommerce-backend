package com.ecommerce.eshop.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "divisions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "bn_name", nullable = false, length = 25)
    private String bnName;

}

