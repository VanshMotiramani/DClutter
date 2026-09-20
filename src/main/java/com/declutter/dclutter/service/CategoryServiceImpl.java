package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.CategoryDTO;
import com.declutter.dclutter.dto.CategoryResponse;
import com.declutter.dclutter.exception.APIException;
import com.declutter.dclutter.exception.ResourceNotFoundException;
import com.declutter.dclutter.model.Category;
import com.declutter.dclutter.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,
                                             String sortBy, String sortOrder) {

        // Create Sort object
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create Pageable object
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Fetch paginated data
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        // Get content from page
        List<Category> categories = categoryPage.getContent();

        // Check if categories exist
        if (categories.isEmpty()) {
            throw new APIException("No categories created till now");
        }

        // Convert entities to DTOs
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());

        // Build response with pagination metadata
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Check if category name already exists
        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryDTO.getCategoryName())) {
            throw new APIException("Category with name '" + categoryDTO.getCategoryName() + "' already exists");
        }

        // Convert DTO to Entity
        Category category = modelMapper.map(categoryDTO, Category.class);

        // Save entity
        Category savedCategory = categoryRepository.save(category);

        // Convert entity back to DTO
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        // Find existing category
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Check if new name conflicts with another category
        if (!existingCategory.getCategoryName().equalsIgnoreCase(categoryDTO.getCategoryName())) {
            if (categoryRepository.existsByCategoryNameIgnoreCase(categoryDTO.getCategoryName())) {
                throw new APIException("Category with name '" + categoryDTO.getCategoryName() + "' already exists");
            }
        }

        // Update entity fields
        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        existingCategory.setDescription(categoryDTO.getDescription());

        // Save updated entity
        Category updatedCategory = categoryRepository.save(existingCategory);

        // Convert to DTO
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        // Find category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Delete category
        categoryRepository.delete(category);

        // Return deleted category as DTO
        return modelMapper.map(category, CategoryDTO.class);
    }
}