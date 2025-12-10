package com.pim.product_management.service;

import com.pim.product_management.dto.request.BrandRequest;
import com.pim.product_management.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {

    BrandResponse createBrand(BrandRequest request);

    BrandResponse getBrandById(Long id);

    BrandResponse getBrandBySlug(String slug);

    List<BrandResponse> getAllBrands();

    BrandResponse updateBrand(Long id, BrandRequest request);

    void deleteBrand(Long id);
}