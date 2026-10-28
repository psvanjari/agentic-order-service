package com.example.agenticorderservice.repository;

import com.example.agenticorderservice.entity.Order;
import com.example.agenticorderservice.entity.OrderTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndRetrieveOrder(){
        Order order= OrderTestFactory.createOrder();

        Order savedOrder=orderRepository.saveAndFlush(order);


        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getStatus().name()).isEqualTo("PENDING");
        assertThat(savedOrder.getCreatedAt()).isNotNull();

        Order retrievedOrder=orderRepository
                .findById(savedOrder.getId())
                .orElseThrow();

        assertThat(retrievedOrder.getCustomerName())
                .isEqualTo("Pratik");
        assertThat(retrievedOrder.getProductName())
                .isEqualTo("Mechanical Keyboard");
        assertThat(retrievedOrder.getUnitPrice())
                .isEqualByComparingTo("2499.00");
        assertThat(retrievedOrder.getQuantity())
                .isEqualTo(2);
    }
}
