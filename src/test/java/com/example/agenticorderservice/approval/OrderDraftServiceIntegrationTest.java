package com.example.agenticorderservice.approval;

import com.example.agenticorderservice.dto.OrderResponse;
import com.example.agenticorderservice.entity.DraftStatus;
import com.example.agenticorderservice.repository.OrderDraftRepository;
import com.example.agenticorderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@Testcontainers
@SpringBootTest
public class OrderDraftServiceIntegrationTest {
    @Container
    static final PostgreSQLContainer POSTGRES=
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("agentic_orders_test")
                    .withUsername("test_user")
                    .withPassword("test_password");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private OrderDraftService orderDraftService;

    @Autowired
    private OrderDraftRepository orderDraftRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanTestDatabase() {
        orderDraftRepository.deleteAll();
        orderRepository.deleteAll();
    }

    private OrderDraftService.PendingOrderDraft createDraft() {
        return orderDraftService.createDraft(
                "Test Customer",
                "Mechanical Keyboard",
                2,
                new BigDecimal("2499.00"));
    }

    @Test
    void shouldPersistDraftInPostgres() {
        var draft = createDraft();

        var savedDraft = orderDraftRepository.findById(draft.draftId());

        assertThat(savedDraft).isPresent();
        assertThat(savedDraft.orElseThrow().getStatus())
                .isEqualTo(DraftStatus.PENDING);
        assertThat(savedDraft.orElseThrow().getCustomerName())
                .isEqualTo("Test Customer");
    }

    @Test
    void shouldConfirmDraftAndCreateOrder(){
        var draft=createDraft();
        OrderResponse order=orderDraftService.confirmDraft(draft.draftId());

        assertThat(order.id()).isNotNull();
        assertThat(order.customerName()).isEqualTo("Test Customer");
        assertThat(order.productName()).isEqualTo("Mechanical Keyboard");
        assertThat(order.quantity()).isEqualTo(2);
        assertThat(order.unitPrice())
                .isEqualByComparingTo("2499.00");

        var savedDraft = orderDraftRepository
                .findById(draft.draftId())
                .orElseThrow();

        assertThat(savedDraft.getStatus())
                .isEqualTo(DraftStatus.CONFIRMED);
        assertThat(savedDraft.getConfirmedOrderId())
                .isEqualTo(order.id());
        assertThat(orderRepository.findById(order.id()))
                .isPresent();
    }

    @Test
    void shouldRejectRepeatedConfirmation() {
        var draft = createDraft();

        OrderResponse firstOrder =
                orderDraftService.confirmDraft(draft.draftId());

        assertThatThrownBy(
                () -> orderDraftService.confirmDraft(draft.draftId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException =
                            (ResponseStatusException) exception;

                    assertThat(responseException.getStatusCode().value())
                            .isEqualTo(409);
                });

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(orderRepository.findById(firstOrder.id()))
                .isPresent();
    }

    @Test
    void shouldPreventConfirmationOfCancelledDraft() {
        var draft = createDraft();

        orderDraftService.cancelDraft(draft.draftId());

        assertThatThrownBy(
                () -> orderDraftService.confirmDraft(draft.draftId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException =
                            (ResponseStatusException) exception;

                    assertThat(responseException.getStatusCode().value())
                            .isEqualTo(409);
                });

        var savedDraft = orderDraftRepository
                .findById(draft.draftId())
                .orElseThrow();

        assertThat(savedDraft.getStatus())
                .isEqualTo(DraftStatus.CANCELLED);
        assertThat(orderRepository.count()).isZero();
    }
}
