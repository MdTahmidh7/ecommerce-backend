package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.ecommerce_backend.payload.request.ProductRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> findByCategoryId(
            Long categoryId,
            Pageable pageable
    );

    Page<ProductResponse> findByCategoryName(
            String categoryName,
            Pageable pageable
    );

    Page<ProductResponse> findByNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    ProductResponse updateProduct(
            Long id,
            ProductRequest productRequest
    );

    void deleteProduct(Long id);

    ProductResponse createProductWithImages(
            ProductRequest productRequest,
            List<MultipartFile> images
    );

    ProductResponse updateProductWithImages(
            Long id,
            ProductRequest productRequest,
            List<MultipartFile> images,
            boolean keepExistingImages
    );

    ProductResponse addProductImages(
            Long id,
            List<MultipartFile> images
    );

    ProductResponse removeProductImage(
            Long id,
            String imageUrl
    );

}
