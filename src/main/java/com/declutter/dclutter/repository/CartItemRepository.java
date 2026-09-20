package com.declutter.dclutter.repository;

import com.declutter.dclutter.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.productId = ?2")
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.product.productId = ?1")
    List<CartItem> findAllByProductId(Long productId);
}