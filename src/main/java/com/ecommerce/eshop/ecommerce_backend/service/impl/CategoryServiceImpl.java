package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.repository.CategoryRepository;
import com.ecommerce.eshop.ecommerce_backend.entity.Category;
import com.ecommerce.eshop.ecommerce_backend.payload.request.CategoryRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.CategoryResponse;
import com.ecommerce.eshop.ecommerce_backend.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageImpl; // Import PageImpl for manual Page creation
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.findByName(categoryRequest.getName()).isPresent() &&
                !categoryRepository.findByName(categoryRequest.getName()).get().isDeleted()) {
            throw new IllegalArgumentException("Category with name '" + categoryRequest.getName() + "' already exists.");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setDeleted(false);
        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {

        Page<Category> categoryPage = categoryRepository.findAllByIsDeletedFalse(pageable);

        List<CategoryResponse> categoryResponses = categoryPage
                .getContent()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        // Create a new PageImpl from the list of DTOs, keeping pagination info
        return new PageImpl<>(categoryResponses, pageable, categoryPage.getTotalElements());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or is deleted with ID: " + id));
        return convertToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or is deleted with ID: " + id));
        Optional<Category> existingCategoryWithName = categoryRepository.findByName(categoryRequest.getName());
        if (existingCategoryWithName.isPresent() &&
                !existingCategoryWithName.get().getId().equals(id) &&
                !existingCategoryWithName.get().isDeleted()) {
            throw new IllegalArgumentException("Category with name '" + categoryRequest.getName() + "' already exists.");
        }
        BeanUtils.copyProperties(categoryRequest, category, "id", "createdAt", "isDeleted");
        category.setUpdatedAt(LocalDateTime.now());
        Category updatedCategory = categoryRepository.save(category);
        return convertToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id) || categoryRepository.findById(id).map(Category::isDeleted).orElse(true)) {
            throw new EntityNotFoundException("Category not found or already deleted with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found or is deleted with ID: " + id));
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}
