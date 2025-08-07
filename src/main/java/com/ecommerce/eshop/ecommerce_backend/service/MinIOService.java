package com.ecommerce.eshop.ecommerce_backend.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class MinIOService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String endpoint;
    private boolean bucketInitialized = false;

    public MinIOService(MinioClient minioClient,
                        @Value("${minio.bucket-name:ecommerce-products}") String bucketName,
                        @Value("${minio.endpoint:http://localhost:9000}") String endpoint) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
    }

    @PostConstruct
    public void initializeBucket() {
        try {
            log.info("Initializing MinIO bucket: {}", bucketName);

            // Check if bucket exists
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                // Create bucket
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Bucket '{}' created successfully", bucketName);

                // Set public read policy
                String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(bucketName);

                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );
                log.info("Public read policy applied to bucket '{}'", bucketName);
            } else {
                log.info("Bucket '{}' already exists", bucketName);
            }

            bucketInitialized = true;

        } catch (Exception e) {
            log.error("Error initializing MinIO bucket: {}", e.getMessage(), e);
            log.warn("MinIO bucket initialization failed, but application will continue");
        }
    }

    /**
     * Upload file to MinIO and return public URL
     */
    public String uploadFile(MultipartFile file, String folder, String customFileName) throws Exception {
        ensureBucketInitialized();
        validateFile(file);

        // Use the provided customFileName directly
        // Use the already unique filename

        String objectName = folder + "/" + customFileName; // This will now be products/uniqueFileName

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Return public URL
            //String publicUrl = endpoint + "/" + bucketName + "/" + objectName;
            log.info("File uploaded successfully: {}", objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    /**
     * Upload product image
     */
    public String uploadProductImage(MultipartFile file, Long productId) throws Exception {
        // Generate a unique filename here.
        // The productId can be incorporated into the filename if you want,
        // but the key is to ensure uniqueness for the flat 'products/' folder.
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Option 1: Using UUID for maximum uniqueness
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Option 2: Incorporate productId and a timestamp (requires sanitization if originalFilename is used)
        // String uniqueFileName = productId + "_" + System.currentTimeMillis() + "_" +
        //                         // Sanitize originalFilename if you include it to remove invalid path characters
        //                         originalFilename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        // Ensure this uniqueFileName is safe for file paths.

        // Pass "products" as the folder, and the uniqueFileName will be used by uploadFile
        return uploadFile(file, "products", uniqueFileName); // Modified to pass uniqueFileName
    }

    /**
     * Delete file from MinIO
     */
    public void deleteFile(String fileUrl) {
        try {
            ensureBucketInitialized();

            // Extract object name from URL
            String objectName = extractObjectNameFromUrl(fileUrl);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            log.info("File deleted successfully: {}", objectName);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
        }
    }

    private void ensureBucketInitialized() throws Exception {
        if (!bucketInitialized) {
            initializeBucket();
            if (!bucketInitialized) {
                throw new RuntimeException("MinIO bucket is not properly initialized");
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size cannot exceed 10MB");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String extractObjectNameFromUrl(String fileUrl) {
        // Extract object name from URL: http://localhost:9000/bucket-name/folder/file.jpg
        String baseUrl = endpoint + "/" + bucketName + "/";
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length());
        }
        throw new IllegalArgumentException("Invalid file URL format");
    }
}
