package com.declutter.dclutter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
    private Double subTotal;
}