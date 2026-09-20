package com.declutter.dclutter.controller;

import com.declutter.dclutter.config.AppConstants;
import com.declutter.dclutter.dto.APIResponse;
import com.declutter.dclutter.dto.ProductDTO;
import com.declutter.dclutter.dto.ProductResponse;
import com.declutter.dclutter.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_ORDER, required = false) String sortOrder) {

        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = "price", required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_ORDER, required = false) String sortOrder) {

        ProductResponse productResponse = productService.getProductsByCategory(
                categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/products/search")
    public ResponseEntity<ProductResponse> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_ORDER, required = false) String sortOrder) {

        ProductResponse productResponse = productService.searchProducts(keyword, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductDTO productDTO) {

        productDTO.setCategoryId(categoryId);
        ProductDTO savedProductDTO = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDTO);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO) {

        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) throws IOException {

        ProductDTO updatedProductDTO = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<APIResponse> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProductDTO = productService.deleteProduct(productId);

        APIResponse response = new APIResponse(
                "Product '" + deletedProductDTO.getProductName() + "' deleted successfully",
                true
        );
        return ResponseEntity.ok(response);
    }
}