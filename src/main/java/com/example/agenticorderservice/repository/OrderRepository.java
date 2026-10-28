package com.example.agenticorderservice.repository;

import com.example.agenticorderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
