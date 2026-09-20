package com.declutter.dclutter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)  // ✅ Changed to LAZY
    @JoinColumn(name = "cart_id")
    @JsonIgnore  // ✅ Added
    @ToString.Exclude
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)  // Keep EAGER for product details
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "product_price")
    private Double productPrice;
}