package com.example.agenticorderservice.approval;

import com.example.agenticorderservice.dto.CreateOrderRequest;
import com.example.agenticorderservice.dto.OrderResponse;
import com.example.agenticorderservice.entity.DraftStatus;
import com.example.agenticorderservice.entity.Order;
import com.example.agenticorderservice.entity.OrderDraft;
import com.example.agenticorderservice.repository.OrderDraftRepository;
import com.example.agenticorderservice.service.OrderService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderDraftService {
    private final OrderService orderService;
    private final OrderDraftRepository draftRepository;
    private final Validator validator;

    public OrderDraftService(
            OrderService orderService,
            OrderDraftRepository draftRepository,
            Validator validator) {
        this.orderService = orderService;
        this.draftRepository = draftRepository;
        this.validator = validator;
    }

    @Transactional
    public PendingOrderDraft createDraft(
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ){
        CreateOrderRequest request=new CreateOrderRequest(
                customerName,
                productName,
                quantity,
                unitPrice
        );

        Set<ConstraintViolation<CreateOrderRequest>> violations=
                validator.validate(request);

        if (!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }

        OrderDraft draft = OrderDraft.create(
                customerName, productName, quantity, unitPrice);

        draftRepository.save(draft);

        return toPendingDraft(draft);
    }

    @Transactional
    public OrderResponse confirmDraft(UUID draftId) {

        OrderDraft draft = draftRepository.findByIdForUpdate(draftId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Draft not found"));

        if (draft.getStatus() != DraftStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Draft has already been confirmed or cancelled");
        }

        Order order = orderService.createOrder(
                draft.getCustomerName(),
                draft.getProductName(),
                draft.getQuantity(),
                draft.getUnitPrice());

        draft.confirm(order.getId());

        draftRepository.save(draft);

        return OrderResponse.from(order);
    }


    @Transactional
    public void cancelDraft(UUID draftId) {

        OrderDraft draft = draftRepository.findByIdForUpdate(draftId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Draft not found"));

        if (draft.getStatus() != DraftStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Draft has already been confirmed or cancelled");
        }

        draft.cancel();
        draftRepository.save(draft);
    }

    private PendingOrderDraft toPendingDraft(OrderDraft draft) {
        return new PendingOrderDraft(
                draft.getDraftId(),
                draft.getCustomerName(),
                draft.getProductName(),
                draft.getQuantity(),
                draft.getUnitPrice(),
                draft.getCreatedAt());
    }

    public record PendingOrderDraft(
            UUID draftId,
            String customerName,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            Instant createdAt) {
    }
}
