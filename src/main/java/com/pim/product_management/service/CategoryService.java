package com.pim.product_management.service;

import com.pim.product_management.dto.request.CategoryRequest;
import com.pim.product_management.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse getCategoryBySlug(String slug);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getCategoryTree();

    List<CategoryResponse> getSubCategories(Long parentId);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    CategoryResponse moveCategory(Long categoryId, Long newParentId);

    void deleteCategory(Long id);
}