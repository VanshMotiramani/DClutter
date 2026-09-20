package com.declutter.dclutter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}