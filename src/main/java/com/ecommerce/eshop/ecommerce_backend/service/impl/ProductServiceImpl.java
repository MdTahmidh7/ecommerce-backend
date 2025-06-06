package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.repository.ProductRepository;
import com.ecommerce.eshop.ecommerce_backend.entity.Category;
import com.ecommerce.eshop.ecommerce_backend.entity.Product;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ProductRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.CategoryResponse;
import com.ecommerce.eshop.ecommerce_backend.payload.response.ProductResponse;
import com.ecommerce.eshop.ecommerce_backend.service.CategoryService;
import com.ecommerce.eshop.ecommerce_backend.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageImpl; // Import PageImpl
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryService categoryService
    ) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        if (productRepository.findByName(productRequest.getName()).isPresent() &&
                !productRepository.findByName(productRequest.getName()).get().isDeleted()) {
            throw new IllegalArgumentException("Product with name '" + productRequest.getName() + "' already exists.");
        }

        Category category = categoryService.getCategoryEntityById(productRequest.getCategoryId());

        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false);

        Product savedProduct = productRepository.save(product);
        return convertToResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByIsDeletedFalse(pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or is deleted with ID: " + id));
        return convertToResponse(product);
    }

    @Override
    public Page<ProductResponse> findByCategoryId(Long categoryId, Pageable pageable) {

        categoryService.getCategoryEntityById(categoryId);

        Page<Product> productPage = productRepository.findByCategoryIdAndIsDeletedFalse(categoryId, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override

    public Page<ProductResponse> findByCategoryName(String categoryName, Pageable pageable) {

        Page<Product> productPage = productRepository.findByCategoryNameAndIsDeletedFalse(categoryName, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductResponse> findByNameContainingIgnoreCase(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or is deleted with ID: " + id));

        Optional<Product> existingProductWithName = productRepository.findByName(productRequest.getName());
        if (existingProductWithName.isPresent() &&
                !existingProductWithName.get().getId().equals(id) &&
                !existingProductWithName.get().isDeleted()) {
            throw new IllegalArgumentException("Product with name '" + productRequest.getName() + "' already exists.");
        }

        Category category = categoryService.getCategoryEntityById(productRequest.getCategoryId());

        BeanUtils.copyProperties(productRequest, product, "id", "createdAt", "isDeleted");
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id) || productRepository.findById(id).map(Product::isDeleted).orElse(true)) {
            throw new EntityNotFoundException("Product not found or already deleted with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response, "category");
        response.setCategory(convertToResponse(product.getCategory()));
        return response;
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}
