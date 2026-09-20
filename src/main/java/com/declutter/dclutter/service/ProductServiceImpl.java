package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.ProductDTO;
import com.declutter.dclutter.dto.ProductResponse;
import com.declutter.dclutter.exception.APIException;
import com.declutter.dclutter.exception.ResourceNotFoundException;
import com.declutter.dclutter.model.Category;
import com.declutter.dclutter.model.Product;
import com.declutter.dclutter.model.ProductStatus;
import com.declutter.dclutter.repository.CategoryRepository;
import com.declutter.dclutter.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String imagePath;

    @Autowired
    private CartService cartService;

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize,
                                          String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findAll(pageable);

        if (productPage.isEmpty()) {
            throw new APIException("No products available");
        }

        return buildProductResponse(productPage);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize,
                                                 String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findByCategory(category, pageable);

        if (productPage.isEmpty()) {
            throw new APIException("No products found in category: " + category.getCategoryName());
        }

        return buildProductResponse(productPage);
    }

    @Override
    public ProductResponse searchProducts(String keyword, Integer pageNumber, Integer pageSize,
                                          String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);

        if (productPage.isEmpty()) {
            throw new APIException("No products found matching: " + keyword);
        }

        return buildProductResponse(productPage);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {

        if (productRepository.existsByProductNameIgnoreCase(productDTO.getProductName())) {
            throw new APIException("Product with name '" + productDTO.getProductName() + "' already exists");
        }

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", productDTO.getCategoryId()));

        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setImage(productDTO.getImage());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setCondition(productDTO.getCondition());
        product.setCategory(category);

        product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : ProductStatus.AVAILABLE);

        double discount = productDTO.getDiscount() != null ? productDTO.getDiscount() : 0.0;
        product.setDiscount(discount);
        product.setSpecialPrice(calculateSpecialPrice(productDTO.getPrice(), discount));

        Product savedProduct = productRepository.save(product);

        return mapToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (!existingProduct.getProductName().equalsIgnoreCase(productDTO.getProductName())) {
            if (productRepository.existsByProductNameIgnoreCase(productDTO.getProductName())) {
                throw new APIException("Product with name '" + productDTO.getProductName() + "' already exists");
            }
        }

        Category category = existingProduct.getCategory();
        if (!existingProduct.getCategory().getCategoryId().equals(productDTO.getCategoryId())) {
            category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", productDTO.getCategoryId()));
        }

        existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setCondition(productDTO.getCondition());
        existingProduct.setStatus(productDTO.getStatus());
        existingProduct.setCategory(category);

        double discount = productDTO.getDiscount() != null ? productDTO.getDiscount() : 0.0;
        existingProduct.setDiscount(discount);
        existingProduct.setSpecialPrice(calculateSpecialPrice(productDTO.getPrice(), discount));

        Product updatedProduct = productRepository.save(existingProduct);

        // ✅ Propagate price/stock changes to all carts
        cartService.updateProductInCarts(productId);

        return mapToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                fileService.deleteImage(imagePath, product.getImage());
            } catch (IOException e) {
                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }

        String filename = fileService.uploadImage(imagePath, image);
        product.setImage(filename);
        Product updatedProduct = productRepository.save(product);

        return mapToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // ✅ Remove product from all carts FIRST (before deleting)
        cartService.removeProductFromAllCarts(productId);

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                fileService.deleteImage(imagePath, product.getImage());
            } catch (IOException e) {
                System.err.println("Failed to delete product image: " + e.getMessage());
            }
        }

        productRepository.delete(product);

        return mapToDTO(product);
    }

    private Double calculateSpecialPrice(Double price, Double discount) {
        return price - (price * discount / 100);
    }

    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setSpecialPrice(product.getSpecialPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCondition(product.getCondition());
        dto.setStatus(product.getStatus());
        dto.setCategoryId(product.getCategory().getCategoryId());
        dto.setCategoryName(product.getCategory().getCategoryName());
        return dto;
    }

    private ProductResponse buildProductResponse(Page<Product> productPage) {
        List<ProductDTO> productDTOs = productPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }
}