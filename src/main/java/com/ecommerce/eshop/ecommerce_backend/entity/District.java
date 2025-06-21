package com.ecommerce.eshop.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "districts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "division_id", nullable = false)
    private Integer divisionId;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "bn_name", nullable = false, length = 25)
    private String bnName;

    @Column(length = 15)
    private String lat;

    @Column(length = 15)
    private String lon;

    @Column(nullable = false, length = 50)
    private String url;
}

