package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.ProductDTO;
import com.declutter.dclutter.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize,
                                          String sortBy, String sortOrder);

    ProductResponse searchProducts(String keyword, Integer pageNumber, Integer pageSize,
                                   String sortBy, String sortOrder);

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductDTO deleteProduct(Long productId);
}