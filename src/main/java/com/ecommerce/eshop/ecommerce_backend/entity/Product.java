package com.ecommerce.eshop.ecommerce_backend.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE products SET is_deleted = TRUE WHERE id=?")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 10000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal price;

    @NotNull
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    // Getters and setters
    @Getter
    @Column(name = "image_url")
    @Size(max = 500)
    private String imageUrl; //primary image URL

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;          // JSON array of multiple image URLs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Product must have a category")
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    // Helper methods for image URLs
    public List<String> getImageUrlsList() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(imageUrls,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setImageUrlsList(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            this.imageUrls = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.imageUrls = mapper.writeValueAsString(urls);
        } catch (Exception e) {
            this.imageUrls = null;
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
