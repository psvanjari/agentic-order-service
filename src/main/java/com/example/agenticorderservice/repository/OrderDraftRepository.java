package com.example.agenticorderservice.repository;

import com.example.agenticorderservice.entity.OrderDraft;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderDraftRepository extends JpaRepository<OrderDraft, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT d
        FROM OrderDraft d
        WHERE d.draftId = :draftId
        """)
    Optional<OrderDraft> findByIdForUpdate(
            @Param("draftId") UUID draftId);
}
