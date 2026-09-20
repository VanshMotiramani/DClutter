package com.declutter.dclutter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "special_price")
    private Double specialPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "condition")
    @Enumerated(EnumType.STRING)
    private ProductCondition condition;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    // Many-to-One with Category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @JsonIgnore
    @ToString.Exclude
    private User seller;
}