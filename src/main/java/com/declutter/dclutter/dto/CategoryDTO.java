package com.declutter.dclutter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;

    @NotBlank(message = "Category name is required and cannot be blank")
    @Size(min = 5, message = "Category name must be at least 5 characters long")
    private String categoryName;

    @NotBlank(message = "Description is required and cannot be blank")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;
}