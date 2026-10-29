package com.example.agenticorderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_drafts")
@Getter
@NoArgsConstructor
public class OrderDraft {
    @Id
    @Column(name = "draft_id", nullable = false, updatable = false)
    private UUID draftId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DraftStatus status = DraftStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "confirmed_order_id", unique = true)
    private UUID confirmedOrderId;

    public static OrderDraft create(
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice) {

        OrderDraft draft = new OrderDraft();
        draft.draftId = UUID.randomUUID();
        draft.customerName = customerName;
        draft.productName = productName;
        draft.quantity = quantity;
        draft.unitPrice = unitPrice;
        draft.status = DraftStatus.PENDING;
        draft.createdAt = Instant.now();

        return draft;
    }

    public void confirm(UUID orderId) {
        if (status != DraftStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending drafts can be confirmed");
        }

        this.status = DraftStatus.CONFIRMED;
        this.confirmedOrderId = orderId;
    }

    public void cancel() {
        if (status != DraftStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending drafts can be cancelled");
        }

        this.status = DraftStatus.CANCELLED;
    }
}
