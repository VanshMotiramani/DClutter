package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.CategoryDTO;
import com.declutter.dclutter.dto.CategoryResponse;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);
}