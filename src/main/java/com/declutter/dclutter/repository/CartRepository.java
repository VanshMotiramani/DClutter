package com.declutter.dclutter.repository;

import com.declutter.dclutter.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.username = ?1")
    Optional<Cart> findCartByUsername(String username);

    @Query("SELECT c FROM Cart c WHERE c.user.username = ?1 AND c.cartId = ?2")
    Optional<Cart> findCartByUsernameAndCartId(String username, Long cartId);
}