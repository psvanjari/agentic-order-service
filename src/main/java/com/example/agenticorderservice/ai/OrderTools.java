package com.example.agenticorderservice.ai;

import com.example.agenticorderservice.approval.OrderDraftService;
import com.example.agenticorderservice.dto.OrderResponse;
import com.example.agenticorderservice.exception.OrderNotFoundException;
import com.example.agenticorderservice.service.OrderService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class OrderTools {
    private final OrderService orderService;
    private final OrderDraftService orderDraftService;

    public OrderTools(
            OrderService orderService,
            OrderDraftService orderDraftService) {
        this.orderService = orderService;
        this.orderDraftService = orderDraftService;
    }

    @Tool(
            description = """
                    Find an order using its exact UUID.
                    Use this tool when the user asks for details about a specific order.
                    The order ID must be a valid UUID.
                    """
    )
    public String getOrderById(String orderId){
        try {
            UUID id = UUID.fromString(orderId);
            OrderResponse order =
                    OrderResponse.from(orderService.getOrderById(id));

            return """
                    Order found:
                    ID: %s
                    Customer: %s
                    Product: %s
                    Quantity: %d
                    Unit price: %s
                    Status: %s
                    Created at: %s
                    """.formatted(
                    order.id(),
                    order.customerName(),
                    order.productName(),
                    order.quantity(),
                    order.unitPrice(),
                    order.status(),
                    order.createdAt()
            );

        } catch (IllegalArgumentException exception) {
            return "Invalid order ID. Please provide a valid UUID.";
        } catch (OrderNotFoundException exception) {
            return "No order was found with ID: " + orderId;
        }
    }

    @Tool(description = """
            Retrieve all orders currently stored in the system.
            Use this tool when the user asks to list, view, or summarize all orders.
            Do not invent orders or order details that are not returned by this tool.
            """)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }


    @Tool(description = """
        Prepare an order draft for the user to review.
        Call this only when the user has provided all four required
        details: customer name, product name, quantity, and unit price.
        This method does NOT create or save an order.
        Return the draft ID and details so the user can explicitly
        confirm the draft through the confirmation endpoint.
        Never assume that the user has confirmed the order.
        """)
    public String proposeOrder(
            @ToolParam(description = "Customer's name")
            String customerName,

            @ToolParam(description = "Product name")
            String productName,

            @ToolParam(description = "Number of units to order")
            Integer quantity,

            @ToolParam(description = "Price per unit, in the application's currency")
            BigDecimal unitPrice) {

        OrderDraftService.PendingOrderDraft draft =
                orderDraftService.createDraft(
                        customerName,
                        productName,
                        quantity,
                        unitPrice
                );

        return """
            Order draft prepared. No order has been created yet.
            Draft ID: %s
            Customer: %s
            Product: %s
            Quantity: %d
            Unit price: %s
            Total: %s

            Ask the user to review these details and explicitly confirm.
            To confirm, send POST /api/ai/order-drafts/%s/confirm.
            To cancel, send DELETE /api/ai/order-drafts/%s.
            """.formatted(
                draft.draftId(),
                draft.customerName(),
                draft.productName(),
                draft.quantity(),
                draft.unitPrice(),
                draft.unitPrice().multiply(
                        BigDecimal.valueOf(draft.quantity())),
                draft.draftId(),
                draft.draftId()
        );
    }


}
