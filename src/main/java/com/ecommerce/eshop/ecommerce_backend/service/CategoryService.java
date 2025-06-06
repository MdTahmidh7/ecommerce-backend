package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.ecommerce_backend.entity.Category;
import com.ecommerce.eshop.ecommerce_backend.payload.request.CategoryRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.CategoryResponse;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);

    void deleteCategory(Long id);

    Category getCategoryEntityById(Long id);

}
