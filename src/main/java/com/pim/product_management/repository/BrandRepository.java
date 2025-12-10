package com.pim.product_management.repository;

import com.pim.product_management.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}