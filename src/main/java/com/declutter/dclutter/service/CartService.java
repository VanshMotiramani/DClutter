package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.CartDTO;
import java.util.List;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart();

    CartDTO updateProductQuantityInCart(Long productId, Integer delta);

    String deleteProductFromCart(Long cartId, Long productId);

    // Called by ProductService when a product changes
    void updateProductInCarts(Long productId);

    // Called by ProductService when a product is deleted
    void removeProductFromAllCarts(Long productId);
}