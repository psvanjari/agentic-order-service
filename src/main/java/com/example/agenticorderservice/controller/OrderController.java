package com.example.agenticorderservice.controller;

import com.example.agenticorderservice.dto.CreateOrderRequest;
import com.example.agenticorderservice.dto.OrderResponse;
import com.example.agenticorderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return OrderResponse.from(
                orderService.createOrder(
                        request.customerName(),
                        request.productName(),
                        request.quantity(),
                        request.unitPrice()
                )
        );
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return OrderResponse.from(
                orderService.getOrderById(id)
        );
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }
}
