package com.declutter.dclutter.controller;

import com.declutter.dclutter.config.AppConstants;
import com.declutter.dclutter.dto.APIResponse;
import com.declutter.dclutter.dto.CategoryDTO;
import com.declutter.dclutter.dto.CategoryResponse;
import com.declutter.dclutter.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            Integer pageNumber,

            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            Integer pageSize,

            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,

            @RequestParam(name = "sortOrder", defaultValue = AppConstants.DEFAULT_SORT_ORDER, required = false)
            String sortOrder) {

        CategoryResponse categoryResponse = categoryService.getAllCategories(
                pageNumber, pageSize, sortBy, sortOrder);

        return ResponseEntity.ok(categoryResponse);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategoryDTO);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<APIResponse> deleteCategory(@PathVariable Long categoryId) {
        CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);

        APIResponse response = new APIResponse(
                "Category '" + deletedCategoryDTO.getCategoryName() + "' deleted successfully",
                true
        );

        return ResponseEntity.ok(response);
    }
}