package com.pim.product_management.controller;

import com.pim.product_management.dto.request.BrandRequest;
import com.pim.product_management.dto.response.BrandResponse;
import com.pim.product_management.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Slf4j
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        log.info("REST request to create brand: {}", request.getName());
        BrandResponse response = brandService.createBrand(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        log.info("REST request to get brand by id: {}", id);
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BrandResponse> getBrandBySlug(@PathVariable String slug) {
        log.info("REST request to get brand by slug: {}", slug);
        BrandResponse response = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        log.info("REST request to get all brands");
        List<BrandResponse> responses = brandService.getAllBrands();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {
        log.info("REST request to update brand with id: {}", id);
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        log.info("REST request to delete brand with id: {}", id);
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}