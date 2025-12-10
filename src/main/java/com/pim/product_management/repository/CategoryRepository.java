package com.pim.product_management.repository;

import com.pim.product_management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findByParentCategoryIsNull();

    List<Category> findByParentCategoryId(Long parentId);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parentCategory IS NULL")
    List<Category> findAllRootCategoriesWithChildren();
}