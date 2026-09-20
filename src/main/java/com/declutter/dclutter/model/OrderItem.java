package com.declutter.dclutter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Order order;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "ordered_product_price")
    private Double orderedProductPrice;
}