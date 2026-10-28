package com.example.agenticorderservice.dto;

import com.example.agenticorderservice.entity.Order;
import com.example.agenticorderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        OrderStatus status,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getProductName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
