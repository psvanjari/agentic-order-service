package com.example.agenticorderservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name must not be blank")
        @Size(max = 100, message = "Customer name must not exceed 100 characters")
        String customerName,

        @NotBlank(message = "Product name must not be blank")
        @Size(max = 150, message = "Product name must not exceed 150 characters")
        String productName,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.00", message = "Unit price must not be negative")
        @Digits(integer = 10, fraction = 2,
                message = "Unit price must have at most 10 integer digits and 2 decimal places")
        BigDecimal unitPrice
) {
}
