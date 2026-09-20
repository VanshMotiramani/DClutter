package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.CartDTO;
import com.declutter.dclutter.dto.CartItemDTO;
import com.declutter.dclutter.dto.ProductDTO;
import com.declutter.dclutter.exception.APIException;
import com.declutter.dclutter.exception.ResourceNotFoundException;
import com.declutter.dclutter.model.*;
import com.declutter.dclutter.repository.CartItemRepository;
import com.declutter.dclutter.repository.CartRepository;
import com.declutter.dclutter.repository.ProductRepository;
import com.declutter.dclutter.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthUtil authUtil;

    // ========== ADD PRODUCT TO CART ==========
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new APIException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateUserCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Validate product availability
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new APIException("Product '" + product.getProductName() + "' is not available");
        }
        if (product.getQuantity() == 0) {
            throw new APIException("Product '" + product.getProductName() + "' is out of stock");
        }

        CartItem existing = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
                .orElse(null);

        int newQty = (existing == null ? 0 : existing.getQuantity()) + quantity;

        if (newQty > product.getQuantity()) {
            throw new APIException("Only " + product.getQuantity() + " unit(s) of '"
                    + product.getProductName() + "' available. You already have "
                    + (existing == null ? 0 : existing.getQuantity()) + " in your cart.");
        }

        if (existing == null) {
            // Create new cart item
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setDiscount(product.getDiscount() != null ? product.getDiscount() : 0.0);
            item.setProductPrice(product.getSpecialPrice());
            cart.getCartItems().add(item);
        } else {
            // Update existing item
            existing.setQuantity(newQty);
            existing.setProductPrice(product.getSpecialPrice());
            existing.setDiscount(product.getDiscount() != null ? product.getDiscount() : 0.0);
        }

        recalculateTotal(cart);
        return mapToDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No carts exist");
        }
        return carts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getUserCart() {
        Cart cart = cartRepository.findCartByUsername(authUtil.loggedInUsername())
                .orElseThrow(() -> new APIException("Your cart is empty. Add a product to create a cart."));
        return mapToDTO(cart);
    }

    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer delta) {
        Cart cart = cartRepository.findCartByUsername(authUtil.loggedInUsername())
                .orElseThrow(() -> new APIException("Cart not found for the current user"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new APIException("Product '" + product.getProductName() + "' is not in your cart"));

        int newQty = item.getQuantity() + delta;

        if (newQty > product.getQuantity()) {
            throw new APIException("Only " + product.getQuantity() + " unit(s) of '"
                    + product.getProductName() + "' available");
        }

        if (newQty <= 0) {
            // Remove item if quantity becomes 0 or negative
            cart.getCartItems().remove(item);
        } else {
            item.setQuantity(newQty);
            item.setProductPrice(product.getSpecialPrice());
            item.setDiscount(product.getDiscount() != null ? product.getDiscount() : 0.0);
        }

        recalculateTotal(cart);
        return mapToDTO(cartRepository.save(cart));
    }

    // ========== DELETE PRODUCT FROM CART ==========
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findCartByUsernameAndCartId(authUtil.loggedInUsername(), cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String productName = item.getProduct().getProductName();
        cart.getCartItems().remove(item);
        recalculateTotal(cart);
        cartRepository.save(cart);

        return "Product '" + productName + "' removed from the cart";
    }

    // ========== UPDATE PRODUCT IN CARTS (When admin updates product) ==========
    @Override
    public void updateProductInCarts(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        List<CartItem> items = cartItemRepository.findAllByProductId(productId);

        for (CartItem item : items) {
            item.setProductPrice(product.getSpecialPrice());
            item.setDiscount(product.getDiscount() != null ? product.getDiscount() : 0.0);

            // Clamp quantity to available stock
            if (item.getQuantity() > product.getQuantity()) {
                item.setQuantity(product.getQuantity());
            }

            Cart cart = item.getCart();

            // Remove if out of stock or unavailable
            if (item.getQuantity() == 0 || product.getStatus() != ProductStatus.AVAILABLE) {
                cart.getCartItems().remove(item);
            }

            recalculateTotal(cart);
            cartRepository.save(cart);
        }
    }

    // ========== REMOVE PRODUCT FROM ALL CARTS (When admin deletes product) ==========
    @Override
    public void removeProductFromAllCarts(Long productId) {
        List<CartItem> items = cartItemRepository.findAllByProductId(productId);

        for (CartItem item : items) {
            Cart cart = item.getCart();
            cart.getCartItems().remove(item);
            recalculateTotal(cart);
            cartRepository.save(cart);
        }
    }

    // ========== HELPER: GET OR CREATE CART ==========
    private Cart getOrCreateUserCart() {
        return cartRepository.findCartByUsername(authUtil.loggedInUsername())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(authUtil.loggedInUser());
                    cart.setTotalPrice(0.0);
                    return cartRepository.save(cart);
                });
    }

    // ========== HELPER: RECALCULATE TOTAL ==========
    private void recalculateTotal(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(i -> i.getProductPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(Math.round(total * 100.0) / 100.0);
    }

    // ========== HELPER: MAP CART TO DTO ==========
    private CartDTO mapToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setTotalPrice(cart.getTotalPrice());
        dto.setItems(cart.getCartItems().stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    // ========== HELPER: MAP CART ITEM TO DTO ==========
    private CartItemDTO mapItemToDTO(CartItem item) {
        Product p = item.getProduct();

        // Build ProductDTO
        ProductDTO pd = new ProductDTO();
        pd.setProductId(p.getProductId());
        pd.setProductName(p.getProductName());
        pd.setDescription(p.getDescription());
        pd.setImage(p.getImage());
        pd.setPrice(p.getPrice());
        pd.setDiscount(p.getDiscount());
        pd.setSpecialPrice(p.getSpecialPrice());
        pd.setQuantity(p.getQuantity());  // Stock quantity
        pd.setCondition(p.getCondition());
        pd.setStatus(p.getStatus());
        pd.setCategoryId(p.getCategory().getCategoryId());
        pd.setCategoryName(p.getCategory().getCategoryName());

        // Build CartItemDTO
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(item.getCartItemId());
        dto.setProduct(pd);
        dto.setQuantity(item.getQuantity());  // Cart quantity
        dto.setDiscount(item.getDiscount());
        dto.setProductPrice(item.getProductPrice());
        dto.setSubTotal(Math.round(item.getProductPrice() * item.getQuantity() * 100.0) / 100.0);

        return dto;
    }
}