package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.payload.request.CategoryRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.CategoryResponse;
import com.ecommerce.eshop.ecommerce_backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; // For default page size/sort
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // For role-based authorization
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories") // Base URL for category endpoints
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a new category.
     * Accessible by ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can create categories
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse newCategory = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    /**
     * Retrieves all non-deleted categories with pagination.
     * Accessible by ADMIN, USER.
     * Example: GET /api/categories?page=0&size=10&sort=name,asc
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')") // ADMIN and USER can view categories
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a single non-deleted category by its ID.
     * Accessible by ADMIN, USER.
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Updates an existing category.
     * Accessible by ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Soft deletes a category by its ID.
     * Accessible by ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
    }
}
