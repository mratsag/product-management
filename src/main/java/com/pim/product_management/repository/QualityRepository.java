package com.pim.product_management.repository;

import com.pim.product_management.entity.Quality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QualityRepository extends JpaRepository<Quality, Long> {

    Optional<Quality> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}