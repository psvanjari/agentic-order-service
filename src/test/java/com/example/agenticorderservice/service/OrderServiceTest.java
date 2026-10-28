package com.example.agenticorderservice.service;

import com.example.agenticorderservice.entity.Order;
import com.example.agenticorderservice.exception.OrderNotFoundException;
import com.example.agenticorderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreatedOrderSuccessfully(){
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation->invocation.getArgument(0));

        Order result=orderService.createOrder(
                "Pratik",
                "Mechanical Keyboard",
                2,
                new BigDecimal("2499.00")
        );

        assertThat(result.getCustomerName()).isEqualTo("Pratik");
        assertThat(result.getProductName())
                .isEqualTo("Mechanical Keyboard");
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnitPrice())
                .isEqualByComparingTo("2499.00");
        assertThat(result.getStatus().name()).isEqualTo("PENDING");

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldRejectZeroQuantity() {
        assertThatThrownBy(() ->
                orderService.createOrder(
                        "Pratik",
                        "Mechanical Keyboard",
                        0,
                        new BigDecimal("2499.00")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be greater than zero");

        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldRejectNegativePrice() {
        assertThatThrownBy(() ->
                orderService.createOrder(
                        "Pratik",
                        "Mechanical Keyboard",
                        2,
                        new BigDecimal("-100.00")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unit price must not be negative");

        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldReturnOrderWhenItExists() {
        UUID orderId = UUID.randomUUID();

        Order expectedOrder = Order.create(
                "Pratik",
                "Mechanical Keyboard",
                2,
                new BigDecimal("2499.00")
        );

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(expectedOrder));

        Order result = orderService.getOrderById(orderId);

        assertThat(result).isSameAs(expectedOrder);

        verify(orderRepository).findById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.getOrderById(orderId)
        )
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found with ID: " + orderId);

        verify(orderRepository).findById(orderId);
    }
}
