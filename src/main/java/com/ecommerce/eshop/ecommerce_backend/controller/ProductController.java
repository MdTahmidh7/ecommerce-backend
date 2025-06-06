package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.payload.request.ProductRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.ProductResponse;
import com.ecommerce.eshop.ecommerce_backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; // For default page size/sort
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // For role-based authorization
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products") // Base URL for product endpoints
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a new product.
     * Accessible by ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest
    ) {
        ProductResponse newProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    /**
     * Retrieves all non-deleted products with pagination.
     * Accessible by ADMIN, USER.
     * Example: GET /api/products?page=0&size=10&sort=name,asc
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a single non-deleted product by its ID.
     * Accessible by ADMIN, USER.
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {

        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Retrieves non-deleted products by Category ID with pagination.
     * Accessible by ADMIN, USER.
     * Example: GET /api/products/category/1?page=0&size=5
     */
    @GetMapping("/category/{categoryId}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.findByCategoryId(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves non-deleted products by Category Name with pagination.
     * Accessible by ADMIN, USER.
     * Example: GET /api/products/category/name/Electronics?page=0&size=5
     */
    @GetMapping("/category/name/{categoryName}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategoryName(
            @PathVariable String categoryName,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.findByCategoryName(categoryName, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Searches for non-deleted products by name (case-insensitive) with pagination.
     * Accessible by ADMIN, USER.
     * Example: GET /api/products/search?keyword=phone&page=0&size=5
     */
    @GetMapping("/search")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<ProductResponse>> searchProductsByName(
            @RequestParam String keyword,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.findByNameContainingIgnoreCase(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Updates an existing product.
     * Accessible by ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Soft deletes a product by its ID.
     * Accessible by ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
