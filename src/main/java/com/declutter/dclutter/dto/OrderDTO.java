package com.declutter.dclutter.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private LocalDate orderDate;
    private PaymentDTO payment;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
    private AddressDTO address;
}