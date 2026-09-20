package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.*;
import com.declutter.dclutter.exception.APIException;
import com.declutter.dclutter.exception.ResourceNotFoundException;
import com.declutter.dclutter.model.*;
import com.declutter.dclutter.repository.*;
import com.declutter.dclutter.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired private CartRepository cartRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private AuthUtil authUtil;

    @Override
    public OrderDTO placeOrder(String paymentMethod, OrderRequestDTO request) {

        String username = authUtil.loggedInUsername();
        String email = authUtil.loggedInEmail();

        // 1. Fetch cart
        Cart cart = cartRepository.findCartByUsername(username)
                .orElseThrow(() -> new APIException("Cart is empty. Add products before placing an order."));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new APIException("Cart is empty. Add products before placing an order.");
        }

        // 2. Fetch address & verify ownership
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", request.getAddressId()));

        boolean ownsAddress = address.getUsers().stream().anyMatch(u -> u.getUsername().equals(username));
        if (!ownsAddress) {
            throw new APIException("The selected address does not belong to you");
        }

        // 3. Validate stock for every item BEFORE creating the order
        for (CartItem ci : cart.getCartItems()) {
            Product p = ci.getProduct();
            if (p.getStatus() != ProductStatus.AVAILABLE) {
                throw new APIException("Product '" + p.getProductName() + "' is no longer available");
            }
            if (ci.getQuantity() > p.getQuantity()) {
                throw new APIException("Only " + p.getQuantity() + " unit(s) of '"
                        + p.getProductName() + "' left in stock");
            }
        }

        // 4. Create payment
        Payment payment = new Payment(
                paymentMethod,
                request.getPgPaymentId(),
                request.getPgStatus(),
                request.getPgResponseMessage(),
                request.getPgName()
        );
        payment = paymentRepository.save(payment);

        // 5. Create order
        Order order = new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        payment.setOrder(savedOrder);
        paymentRepository.save(payment);

        // 6. Cart items -> order items + reduce stock
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cart.getCartItems()) {
            OrderItem oi = new OrderItem();
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setDiscount(ci.getDiscount());
            oi.setOrderedProductPrice(ci.getProductPrice());
            oi.setOrder(savedOrder);
            orderItems.add(oi);

            // reduce stock
            Product p = ci.getProduct();
            p.setQuantity(p.getQuantity() - ci.getQuantity());
            if (p.getQuantity() == 0) {
                p.setStatus(ProductStatus.SOLD);   // pre-loved item sold out
            }
            productRepository.save(p);
        }
        orderItems = orderItemRepository.saveAll(orderItems);
        savedOrder.setOrderItems(orderItems);

        // 7. Clear the cart
        cart.getCartItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getMyOrders() {
        List<Order> orders = orderRepository.findAllByEmailOrderByOrderDateDesc(authUtil.loggedInEmail());
        if (orders.isEmpty()) {
            throw new APIException("You have not placed any order yet");
        }
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

        if (!order.getEmail().equals(authUtil.loggedInEmail())) {
            throw new APIException("You are not allowed to view this order");
        }
        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new APIException("No orders placed till now");
        }
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ---------- mappers ----------
    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setEmail(order.getEmail());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus());

        if (order.getAddress() != null) {
            Address a = order.getAddress();
            dto.setAddressId(a.getAddressId());
            dto.setAddress(new AddressDTO(a.getAddressId(), a.getStreet(), a.getBuildingName(),
                    a.getCity(), a.getState(), a.getCountry(), a.getPincode()));
        }

        if (order.getPayment() != null) {
            Payment p = order.getPayment();
            dto.setPayment(new PaymentDTO(p.getPaymentId(), p.getPaymentMethod(), p.getPgPaymentId(),
                    p.getPgStatus(), p.getPgResponseMessage(), p.getPgName()));
        }

        dto.setOrderItems(order.getOrderItems().stream().map(this::mapItemToDTO).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO mapItemToDTO(OrderItem item) {
        Product p = item.getProduct();

        ProductDTO pd = new ProductDTO();
        pd.setProductId(p.getProductId());
        pd.setProductName(p.getProductName());
        pd.setDescription(p.getDescription());
        pd.setImage(p.getImage());
        pd.setPrice(p.getPrice());
        pd.setDiscount(p.getDiscount());
        pd.setSpecialPrice(p.getSpecialPrice());
        pd.setQuantity(p.getQuantity());   // remaining stock
        pd.setCondition(p.getCondition());
        pd.setStatus(p.getStatus());
        pd.setCategoryId(p.getCategory().getCategoryId());
        pd.setCategoryName(p.getCategory().getCategoryName());

        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItemId(item.getOrderItemId());
        dto.setProduct(pd);
        dto.setQuantity(item.getQuantity());
        dto.setDiscount(item.getDiscount());
        dto.setOrderedProductPrice(item.getOrderedProductPrice());
        dto.setSubTotal(Math.round(item.getOrderedProductPrice() * item.getQuantity() * 100.0) / 100.0);
        return dto;
    }
}