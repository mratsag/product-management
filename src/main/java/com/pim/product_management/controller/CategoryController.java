package com.pim.product_management.controller;

import com.pim.product_management.dto.request.CategoryRequest;
import com.pim.product_management.dto.response.CategoryResponse;
import com.pim.product_management.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("REST request to create category: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("REST request to get category by id: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        log.info("REST request to get category by slug: {}", slug);
        CategoryResponse response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("REST request to get all categories");
        List<CategoryResponse> responses = categoryService.getAllCategories();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        log.info("REST request to get category tree");
        List<CategoryResponse> responses = categoryService.getCategoryTree();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(@PathVariable Long parentId) {
        log.info("REST request to get subcategories for parent id: {}", parentId);
        List<CategoryResponse> responses = categoryService.getSubCategories(parentId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        log.info("REST request to update category with id: {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}/move")
    public ResponseEntity<CategoryResponse> moveCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Long newParentId) {
        log.info("REST request to move category {} to new parent {}", categoryId, newParentId);
        CategoryResponse response = categoryService.moveCategory(categoryId, newParentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("REST request to delete category with id: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}