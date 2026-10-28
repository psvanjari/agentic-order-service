package com.example.agenticorderservice.entity;

import java.math.BigDecimal;

public class OrderTestFactory {

    private OrderTestFactory() {
    }

    public static Order createOrder() {
        Order order = new Order();

        order.setCustomerName("Pratik");
        order.setProductName("Mechanical Keyboard");
        order.setQuantity(2);
        order.setUnitPrice(new BigDecimal("2499.00"));

        return order;
    }
}