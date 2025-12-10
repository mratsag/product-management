package com.pim.product_management.service.impl;

import com.pim.product_management.dto.request.CategoryRequest;
import com.pim.product_management.dto.response.CategoryResponse;
import com.pim.product_management.entity.Category;
import com.pim.product_management.exception.DuplicateResourceException;
import com.pim.product_management.exception.ResourceNotFoundException;
import com.pim.product_management.repository.CategoryRepository;
import com.pim.product_management.service.CategoryService;
import com.pim.product_management.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating category with name: {}", request.getName());

        // Generate slug
        String slug = SlugGenerator.generateSlug(request.getName());

        // Check if slug already exists
        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        // Get parent category if specified
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentCategoryId()));
        }

        // Create category entity
        Category category = Category.builder()
                .parentCategory(parentCategory)
                .name(request.getName())
                .description(request.getDescription())
                .slug(slug)
                .order(request.getOrder() != null ? request.getOrder() : 0)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());

        return mapToResponse(savedCategory, false);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return mapToResponse(category, true);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.debug("Fetching category with slug: {}", slug);

        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));

        return mapToResponse(category, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all categories");

        return categoryRepository.findAll().stream()
                .map(category -> mapToResponse(category, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        log.debug("Fetching category tree");

        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();

        return rootCategories.stream()
                .map(category -> mapToResponseWithChildren(category))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubCategories(Long parentId) {
        log.debug("Fetching subcategories for parent id: {}", parentId);

        // Verify parent exists
        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Category", "id", parentId);
        }

        List<Category> subCategories = categoryRepository.findByParentCategoryId(parentId);

        return subCategories.stream()
                .map(category -> mapToResponse(category, false))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.debug("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Update fields
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getOrder() != null) {
            category.setOrder(request.getOrder());
        }

        // Regenerate slug if name changed
        if (!category.getName().equals(request.getName())) {
            String newSlug = SlugGenerator.generateSlug(request.getName());
            if (categoryRepository.existsBySlug(newSlug)) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            category.setSlug(newSlug);
        }

        // Update parent if specified and different
        if (request.getParentCategoryId() != null &&
                !request.getParentCategoryId().equals(category.getParentCategory() != null ? category.getParentCategory().getId() : null)) {

            // Prevent setting category as its own parent
            if (request.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            // Prevent circular reference
            if (isDescendant(id, request.getParentCategoryId())) {
                throw new IllegalArgumentException("Cannot set a descendant as parent (circular reference)");
            }

            Category newParent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentCategoryId()));
            category.setParentCategory(newParent);
        } else if (request.getParentCategoryId() == null) {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());

        return mapToResponse(updatedCategory, true);
    }

    @Override
    public CategoryResponse moveCategory(Long categoryId, Long newParentId) {
        log.debug("Moving category {} to new parent {}", categoryId, newParentId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (newParentId != null) {
            // Prevent setting category as its own parent
            if (newParentId.equals(categoryId)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            // Prevent circular reference
            if (isDescendant(categoryId, newParentId)) {
                throw new IllegalArgumentException("Cannot set a descendant as parent (circular reference)");
            }

            Category newParent = categoryRepository.findById(newParentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", newParentId));
            category.setParentCategory(newParent);
        } else {
            // Move to root level
            category.setParentCategory(null);
        }

        Category movedCategory = categoryRepository.save(category);
        log.info("Category moved successfully with id: {}", movedCategory.getId());

        return mapToResponse(movedCategory, true);
    }

    @Override
    public void deleteCategory(Long id) {
        log.debug("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check if category has subcategories
        List<Category> subCategories = categoryRepository.findByParentCategoryId(id);
        if (!subCategories.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories. Delete subcategories first.");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }

    private boolean isDescendant(Long ancestorId, Long descendantId) {
        Category current = categoryRepository.findById(descendantId).orElse(null);

        while (current != null && current.getParentCategory() != null) {
            if (current.getParentCategory().getId().equals(ancestorId)) {
                return true;
            }
            current = current.getParentCategory();
        }

        return false;
    }

    private CategoryResponse mapToResponse(Category category, boolean includeSubCategories) {
        CategoryResponse.CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .order(category.getOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());

        if (includeSubCategories && category.getSubCategories() != null) {
            List<CategoryResponse> subCategoryResponses = category.getSubCategories().stream()
                    .map(sub -> mapToResponse(sub, false))
                    .collect(Collectors.toList());
            builder.subCategories(subCategoryResponses);
        }

        return builder.build();
    }

    private CategoryResponse mapToResponseWithChildren(Category category) {
        List<CategoryResponse> children = new ArrayList<>();

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            children = category.getSubCategories().stream()
                    .map(this::mapToResponseWithChildren)
                    .collect(Collectors.toList());
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .order(category.getOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .subCategories(children)
                .build();
    }
}