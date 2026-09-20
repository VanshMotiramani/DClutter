package com.declutter.dclutter.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "order_date")
    private LocalDate orderDate;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    @ToString.Exclude
    private Payment payment;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "order_status")
    private String orderStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @ToString.Exclude
    private Address address;
}