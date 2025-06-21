package com.ecommerce.eshop.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "upazilas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Upazila {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "bn_name", nullable = false, length = 25)
    private String bnName;

    @Column(nullable = false, length = 50)
    private String url;
}

