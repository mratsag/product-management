package com.pim.product_management.repository;

import com.pim.product_management.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    List<ProductAttribute> findByProductId(Long productId);

    Optional<ProductAttribute> findByProductIdAndKey(Long productId, String key);

    void deleteByProductId(Long productId);
}