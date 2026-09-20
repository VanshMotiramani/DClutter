package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.OrderDTO;
import com.declutter.dclutter.dto.OrderRequestDTO;

import java.util.List;

public interface OrderService {

    OrderDTO placeOrder(String paymentMethod, OrderRequestDTO orderRequest);

    List<OrderDTO> getMyOrders();

    OrderDTO getOrderById(Long orderId);

    List<OrderDTO> getAllOrders();   // admin
}