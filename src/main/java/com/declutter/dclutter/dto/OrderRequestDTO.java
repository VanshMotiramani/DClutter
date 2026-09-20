package com.declutter.dclutter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "Address id is required")
    private Long addressId;

    private String paymentMethod;
    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
}