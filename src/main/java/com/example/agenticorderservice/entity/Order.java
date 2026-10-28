package com.example.agenticorderservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(nullable = false,length = 100)
    private String customerName;

    @Setter
    @Column(nullable = false,length = 150)
    private String productName;

    @Setter
    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private OrderStatus status=OrderStatus.PENDING;

    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate(){
        if (createdAt==null){
            createdAt=Instant.now();
        }
    }
}
