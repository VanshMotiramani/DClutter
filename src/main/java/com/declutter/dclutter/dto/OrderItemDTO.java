package com.declutter.dclutter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;
    private Double subTotal;
}