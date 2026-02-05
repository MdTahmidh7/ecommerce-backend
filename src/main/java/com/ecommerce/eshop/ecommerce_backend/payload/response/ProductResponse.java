package com.ecommerce.eshop.ecommerce_backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;           // Primary image
    private List<String> imageUrls;    // All images
    private CategoryResponse category; // Nested CategoryResponse DTO
    private String youtubeLink;

}
