// Updated ProductController.java

package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.payload.request.ProductRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.ProductResponse;
import com.ecommerce.eshop.ecommerce_backend.service.ProductService;
import com.ecommerce.eshop.ecommerce_backend.service.MinIOService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final MinIOService minIOService;

    public ProductController(ProductService productService, MinIOService minIOService) {
        this.productService = productService;
        this.minIOService = minIOService;
    }

    /**
     * Creates a new product with optional images
     * Accessible by ADMIN.
     */
    @PostMapping(consumes = {"multipart/form-data"})
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            ProductResponse newProduct = productService.createProductWithImages(productRequest, images);
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all non-deleted products with pagination.
     * Returns products with image URLs that can be directly used in <img> tags
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a single product by ID with all image URLs
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Updates an existing product and optionally replaces images
     */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "keepExistingImages", defaultValue = "false") boolean keepExistingImages
    ) {
        try {
            ProductResponse updatedProduct = productService.updateProductWithImages(
                    id, productRequest, images, keepExistingImages);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add images to existing product
     */
    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> addProductImages(
            @PathVariable Long id,
            @RequestPart("images") List<MultipartFile> images
    ) {
        try {
            ProductResponse updatedProduct = productService.addProductImages(id, images);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove specific image from product
     */
    @DeleteMapping("/{id}/images")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> removeProductImage(
            @PathVariable Long id,
            @RequestParam String imageUrl
    ) {
        try {
            ProductResponse updatedProduct = productService.removeProductImage(id, imageUrl);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ... rest of existing methods remain unchanged
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.findByCategoryId(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/name/{categoryName}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryName(
            @PathVariable String categoryName,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.findByCategoryName(categoryName, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProductsByName(
            @RequestParam String keyword,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.findByNameContainingIgnoreCase(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
