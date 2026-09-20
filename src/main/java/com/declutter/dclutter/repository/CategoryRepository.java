package com.declutter.dclutter.repository;

import com.declutter.dclutter.model.Category;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}
