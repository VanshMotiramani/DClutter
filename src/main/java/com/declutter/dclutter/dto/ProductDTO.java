package com.declutter.dclutter.dto;

import com.declutter.dclutter.model.ProductCondition;
import com.declutter.dclutter.model.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;

    @NotBlank(message = "Product name is required")
    @Size(min = 3, message = "Product name must be at least 3 characters")
    private String productName;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    private String image;  // Image filename/URL

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    @DecimalMin(value = "0.0", message = "Discount must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    private Double discount;

    private Double specialPrice;  // Calculated field (price - discount)

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

    @NotNull(message = "Condition is required")
    private ProductCondition condition;

    private ProductStatus status;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;
}