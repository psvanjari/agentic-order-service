package com.example.agenticorderservice.service;

import com.example.agenticorderservice.entity.Order;
import com.example.agenticorderservice.exception.OrderNotFoundException;
import com.example.agenticorderservice.repository.OrderRepository;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ){
        validateOrder(customerName, productName, quantity, unitPrice);

        Order order = Order.create(
                customerName,
                productName,
                quantity,
                unitPrice
        );
        return orderRepository.save(order);
    }

    public Order getOrderById(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException(
                    "Order ID must not be null"
            );
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderId)
                );
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private void validateOrder(
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name must not be blank"
            );
        }

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name must not be blank"
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "Unit price must not be negative"
            );
        }
    }
}
