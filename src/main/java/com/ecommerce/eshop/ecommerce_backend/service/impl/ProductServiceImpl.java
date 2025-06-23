package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.repository.ProductRepository;
import com.ecommerce.eshop.ecommerce_backend.entity.Category;
import com.ecommerce.eshop.ecommerce_backend.entity.Product;
import com.ecommerce.eshop.ecommerce_backend.payload.request.ProductRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.CategoryResponse;
import com.ecommerce.eshop.ecommerce_backend.payload.response.ProductResponse;
import com.ecommerce.eshop.ecommerce_backend.service.CategoryService;
import com.ecommerce.eshop.ecommerce_backend.service.MinIOService;
import com.ecommerce.eshop.ecommerce_backend.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageImpl; // Import PageImpl
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final MinIOService minIOService;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryService categoryService, MinIOService minIOService
    ) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.minIOService = minIOService;
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

    @Override
    @Transactional
    public ProductResponse createProductWithImages(ProductRequest productRequest, List<MultipartFile> images) {
        // Validate product name uniqueness
        if (productRepository.findByName(productRequest.getName()).isPresent() &&
                !productRepository.findByName(productRequest.getName()).get().isDeleted()) {
            throw new IllegalArgumentException("Product with name '" + productRequest.getName() + "' already exists.");
        }

        // Get category
        Category category = categoryService.getCategoryEntityById(productRequest.getCategoryId());

        // Create and save product first to get ID
        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false);

        // Save product to get ID for image upload
        Product savedProduct = productRepository.save(product);

        // Upload images if provided
        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = uploadImages(savedProduct.getId(), images);
            if (!imageUrls.isEmpty()) {
                savedProduct.setImageUrl(imageUrls.get(0)); // Set first image as primary
                savedProduct.setImageUrlsList(imageUrls);
                savedProduct = productRepository.save(savedProduct);
            }
        }

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProductWithImages(
            Long id,
            ProductRequest productRequest,
            List<MultipartFile> images,
            boolean keepExistingImages
    ) {

        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found or is deleted with ID: " + id));

        // Validate name uniqueness
        productRepository.findByName(productRequest.getName())
                .filter(p -> !p.getId().equals(id) && !p.isDeleted())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Product with name '" + productRequest.getName() + "' already exists.");
                });

        // Get category
        Category category = categoryService.getCategoryEntityById(productRequest.getCategoryId());

        // Update product details
        BeanUtils.copyProperties(productRequest, product, "id", "createdAt", "isDeleted", "imageUrl", "imageUrls");
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());

        // Handle image updates
        List<String> existingImageUrls = keepExistingImages ? product.getImageUrlsList() : new ArrayList<>();

        // Delete old images if not keeping them
        if (!keepExistingImages && product.getImageUrlsList() != null) {
            for (String imageUrl : product.getImageUrlsList()) {
                try {
                    minIOService.deleteFile(imageUrl);
                } catch (Exception e) {
                    // Log error but continue
                    System.err.println("Failed to delete image: " + imageUrl);
                }
            }
        }

        // Upload new images
        if (images != null && !images.isEmpty()) {
            List<String> newImageUrls = uploadImages(id, images);
            existingImageUrls.addAll(newImageUrls);
        }

        // Update product with image URLs
        if (!existingImageUrls.isEmpty()) {
            product.setImageUrl(existingImageUrls.get(0)); // First image as primary
            product.setImageUrlsList(existingImageUrls);
        } else if (!keepExistingImages) {
            product.setImageUrl(null);
            product.setImageUrlsList(new ArrayList<>());
        }

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse addProductImages(Long productId, List<MultipartFile> images) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        List<String> existingUrls = product.getImageUrlsList();
        List<String> newImageUrls = uploadImages(productId, images);

        existingUrls.addAll(newImageUrls);

        // Set primary image if none exists
        if (product.getImageUrl() == null && !existingUrls.isEmpty()) {
            product.setImageUrl(existingUrls.get(0));
        }

        product.setImageUrlsList(existingUrls);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse removeProductImage(Long productId, String imageUrl) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        List<String> imageUrls = product.getImageUrlsList();
        if (imageUrls.remove(imageUrl)) {
            // Delete from MinIO
            try {
                minIOService.deleteFile(imageUrl);
            } catch (Exception e) {
                System.err.println("Failed to delete image from MinIO: " + imageUrl);
            }

            // Update primary image if it was removed
            if (imageUrl.equals(product.getImageUrl())) {
                product.setImageUrl(imageUrls.isEmpty() ? null : imageUrls.get(0));
            }

            product.setImageUrlsList(imageUrls);
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }

        return convertToResponse(product);
    }

    private List<String> uploadImages(Long productId, List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                String imageUrl = minIOService.uploadProductImage(image, productId);
                imageUrls.add(imageUrl);
            } catch (Exception e) {
                System.err.println("Failed to upload image: " + e.getMessage());
                // Continue with other images
            }
        }

        return imageUrls;
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response, "category");
        response.setCategory(convertToResponse(product.getCategory()));

        // Set image URLs
        response.setImageUrl(product.getImageUrl());
        response.setImageUrls(product.getImageUrlsList());

        return response;
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}
