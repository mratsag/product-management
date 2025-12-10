package com.pim.product_management.service.impl;

import com.pim.product_management.dto.request.ProductRequest;
import com.pim.product_management.dto.response.*;
import com.pim.product_management.entity.*;
import com.pim.product_management.enums.ProductStatus;
import com.pim.product_management.exception.DuplicateResourceException;
import com.pim.product_management.exception.ResourceNotFoundException;
import com.pim.product_management.repository.*;
import com.pim.product_management.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductImageRepository productImageRepository;
    private final QualityRepository qualityRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.debug("Creating product with barcode: {}", request.getBarcode());

        // Check if barcode already exists
        if (productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException("Product", "barcode", request.getBarcode());
        }

        // Verify category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Verify brand exists
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));

        // Create product entity
        Product product = Product.builder()
                .barcode(request.getBarcode())
                .category(category)
                .brand(brand)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.DRAFT)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByBarcode(String barcode) {
        log.debug("Fetching product with barcode: {}", barcode);

        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "barcode", barcode));

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products");

        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByStatus(ProductStatus status) {
        log.debug("Fetching products with status: {}", status);

        return productRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        log.debug("Fetching products for category id: {}", categoryId);

        // Verify category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByBrand(Long brandId) {
        log.debug("Fetching products for brand id: {}", brandId);

        // Verify brand exists
        if (!brandRepository.existsById(brandId)) {
            throw new ResourceNotFoundException("Brand", "id", brandId);
        }

        return productRepository.findByBrandId(brandId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        log.debug("Searching products with keyword: {}", keyword);

        return productRepository.searchByKeyword(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.debug("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Check if new barcode already exists (excluding current product)
        if (!product.getBarcode().equals(request.getBarcode()) &&
                productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException("Product", "barcode", request.getBarcode());
        }

        // Verify category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Verify brand exists
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));

        // Update product
        product.setBarcode(request.getBarcode());
        product.setCategory(category);
        product.setBrand(brand);
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updatedProduct.getId());

        return mapToResponse(updatedProduct);
    }

    @Override
    public ProductResponse updateProductStatus(Long id, ProductStatus status) {
        log.debug("Updating product status for id: {} to {}", id, status);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);

        log.info("Product status updated successfully for id: {}", id);

        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    private ProductResponse mapToResponse(Product product) {
        // Map attributes
        List<ProductAttributeResponse> attributes = productAttributeRepository
                .findByProductId(product.getId()).stream()
                .map(attr -> ProductAttributeResponse.builder()
                        .id(attr.getId())
                        .key(attr.getKey())
                        .value(attr.getValue())
                        .build())
                .collect(Collectors.toList());

        // Map images
        List<ProductImageResponse> images = productImageRepository
                .findByProductIdOrderByOrderAsc(product.getId()).stream()
                .map(img -> ProductImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .altText(img.getAltText())
                        .order(img.getOrder())
                        .build())
                .collect(Collectors.toList());

        // Map quality
        QualityResponse qualityResponse = qualityRepository
                .findByProductId(product.getId())
                .map(quality -> QualityResponse.builder()
                        .id(quality.getId())
                        .score(quality.getScore())
                        .result(quality.getResult())
                        .createdAt(quality.getCreatedAt())
                        .updatedAt(quality.getUpdatedAt())
                        .build())
                .orElse(null);

        return ProductResponse.builder()
                .id(product.getId())
                .barcode(product.getBarcode())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .title(product.getTitle())
                .description(product.getDescription())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .attributes(attributes)
                .images(images)
                .quality(qualityResponse)
                .build();
    }
}