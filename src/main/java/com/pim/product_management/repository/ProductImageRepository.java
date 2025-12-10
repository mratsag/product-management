package com.pim.product_management.repository;

import com.pim.product_management.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByOrderAsc(Long productId);

    void deleteByProductId(Long productId);
}