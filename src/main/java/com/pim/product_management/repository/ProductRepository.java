package com.pim.product_management.repository;

import com.pim.product_management.entity.Product;
import com.pim.product_management.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}