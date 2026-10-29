package com.example.agenticorderservice.controller;

import com.example.agenticorderservice.approval.OrderDraftService;
import com.example.agenticorderservice.dto.OrderResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/order-drafts")
public class OrderDraftController {
    private final OrderDraftService orderDraftService;

    public OrderDraftController(OrderDraftService orderDraftService) {
        this.orderDraftService = orderDraftService;
    }

    @PostMapping("/{draftId}/confirm")
    public OrderResponse confirmDraft(@PathVariable UUID draftId) {
        return orderDraftService.confirmDraft(draftId);
    }

    @DeleteMapping("/{draftId}")
    public void cancelDraft(@PathVariable UUID draftId) {
        orderDraftService.cancelDraft(draftId);
    }
}
