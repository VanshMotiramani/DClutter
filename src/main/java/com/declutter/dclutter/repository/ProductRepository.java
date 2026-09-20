package com.declutter.dclutter.repository;

import com.declutter.dclutter.model.Category;
import com.declutter.dclutter.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by category
    Page<Product> findByCategory(Category category, Pageable pageable);

    // Find products by category ID
    Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);

    // Search products by name (case-insensitive, partial match)
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    // Check if product name exists
    boolean existsByProductNameIgnoreCase(String productName);
}