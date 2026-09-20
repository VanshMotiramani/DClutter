package com.declutter.dclutter.controller;

import com.declutter.dclutter.dto.OrderDTO;
import com.declutter.dclutter.dto.OrderRequestDTO;
import com.declutter.dclutter.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod,
                                               @Valid @RequestBody OrderRequestDTO orderRequest) {
        OrderDTO order = orderService.placeOrder(paymentMethod, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/orders/users")
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}