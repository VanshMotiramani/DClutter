package com.declutter.dclutter.controller;

import com.declutter.dclutter.dto.APIResponse;
import com.declutter.dclutter.dto.CartDTO;
import com.declutter.dclutter.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity) {

        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDTO);
    }

    @GetMapping("/carts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return ResponseEntity.ok(cartDTOs);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getUserCart() {
        CartDTO cartDTO = cartService.getUserCart();
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(
            @PathVariable Long productId,
            @PathVariable String operation) {

        int delta;
        switch (operation.toLowerCase()) {
            case "increase":
                delta = 1;
                break;
            case "decrease":
            case "delete":
                delta = -1;
                break;
            default:
                throw new com.declutter.dclutter.exception.APIException(
                        "Invalid operation '" + operation + "'. Use 'increase' or 'decrease'");
        }

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, delta);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<APIResponse> deleteProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId) {

        String message = cartService.deleteProductFromCart(cartId, productId);
        return ResponseEntity.ok(new APIResponse(message, true));
    }
}