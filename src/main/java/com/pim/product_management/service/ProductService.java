package com.pim.product_management.service;

import com.pim.product_management.dto.request.ProductRequest;
import com.pim.product_management.dto.response.ProductResponse;
import com.pim.product_management.enums.ProductStatus;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductByBarcode(String barcode);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getProductsByStatus(ProductStatus status);

    List<ProductResponse> getProductsByCategory(Long categoryId);

    List<ProductResponse> getProductsByBrand(Long brandId);

    List<ProductResponse> searchProducts(String keyword);

    ProductResponse updateProduct(Long id, ProductRequest request);

    ProductResponse updateProductStatus(Long id, ProductStatus status);

    void deleteProduct(Long id);
}