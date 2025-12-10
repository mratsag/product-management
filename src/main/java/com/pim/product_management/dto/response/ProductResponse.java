package com.pim.product_management.dto.response;

import com.pim.product_management.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String barcode;

    private Long categoryId;
    private String categoryName;

    private Long brandId;
    private String brandName;

    private String title;
    private String description;
    private ProductStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductAttributeResponse> attributes;
    private List<ProductImageResponse> images;
    private QualityResponse quality;
}