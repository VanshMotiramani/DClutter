package com.declutter.dclutter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne(mappedBy = "payment")
    @ToString.Exclude
    private Order order;

    @NotBlank
    @Size(min = 3, message = "Payment method must be at least 3 characters")
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "pg_payment_id")
    private String pgPaymentId;

    @Column(name = "pg_status")
    private String pgStatus;

    @Column(name = "pg_response_message")
    private String pgResponseMessage;

    @Column(name = "pg_name")
    private String pgName;

    public Payment(String paymentMethod, String pgPaymentId, String pgStatus,
                   String pgResponseMessage, String pgName) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgStatus = pgStatus;
        this.pgResponseMessage = pgResponseMessage;
        this.pgName = pgName;
    }
}