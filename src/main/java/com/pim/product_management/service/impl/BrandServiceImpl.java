package com.pim.product_management.service.impl;

import com.pim.product_management.dto.request.BrandRequest;
import com.pim.product_management.dto.response.BrandResponse;
import com.pim.product_management.entity.Brand;
import com.pim.product_management.exception.DuplicateResourceException;
import com.pim.product_management.exception.ResourceNotFoundException;
import com.pim.product_management.repository.BrandRepository;
import com.pim.product_management.service.BrandService;
import com.pim.product_management.util.SlugGenerator;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        log.debug("Creating brand with name: {}", request.getName());

        // Check if brand name already exists
        if (brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Brand", "name", request.getName());
        }

        // Generate slug
        String slug = SlugGenerator.generateSlug(request.getName());

        // Check if slug already exists
        if (brandRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        // Create brand entity
        Brand brand = Brand.builder()
                .name(request.getName())
                .slug(slug)
                .build();

        Brand savedBrand = brandRepository.save(brand);
        log.info("Brand created successfully with id: {}", savedBrand.getId());

        return mapToResponse(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        log.debug("Fetching brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));

        return mapToResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandBySlug(String slug) {
        log.debug("Fetching brand with slug: {}", slug);

        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "slug", slug));

        return mapToResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        log.debug("Fetching all brands");

        return brandRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        log.debug("Updating brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));

        // Check if new name already exists (excluding current brand)
        if (!brand.getName().equals(request.getName()) &&
                brandRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Brand", "name", request.getName());
        }

        // Update brand
        brand.setName(request.getName());

        // Regenerate slug if name changed
        if (!brand.getName().equals(request.getName())) {
            String newSlug = SlugGenerator.generateSlug(request.getName());
            if (brandRepository.existsBySlug(newSlug)) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            brand.setSlug(newSlug);
        }

        Brand updatedBrand = brandRepository.save(brand);
        log.info("Brand updated successfully with id: {}", updatedBrand.getId());

        return mapToResponse(updatedBrand);
    }

    @Override
    public void deleteBrand(Long id) {
        log.debug("Deleting brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));

        brandRepository.delete(brand);
        log.info("Brand deleted successfully with id: {}", id);
    }

    private BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}