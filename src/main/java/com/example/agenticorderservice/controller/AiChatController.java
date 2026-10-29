package com.example.agenticorderservice.controller;

import com.example.agenticorderservice.ai.OrderTools;
import com.example.agenticorderservice.dto.ChatRequest;
import com.example.agenticorderservice.dto.ChatResponse;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final ChatClient chatClient;
    private final OrderTools orderTools;

    public AiChatController(ChatClient.Builder chatClientBuilder,OrderTools orderTools){
        this.chatClient=chatClientBuilder.build();
        this.orderTools=orderTools;
    }


    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatClient.prompt()
                .system("""
                    You are a helpful order-management assistant.

                    You can retrieve real orders and prepare order drafts
                    using the available tools.

                    ORDER CREATION RULES:
                    1. Never create or claim to have created an order
                       during a chat response.
                    2. When the user requests an order, collect the
                       customer name, product name, quantity, and
                       unit price.
                    3. If any detail is missing or ambiguous, ask a
                       clarification question instead of guessing.
                    4. Once all details are explicit, call proposeOrder
                       to prepare a draft.
                    5. Show the draft ID, all order details, and total.
                    6. Clearly tell the user that no order exists yet
                       and that explicit confirmation is required.
                    7. Never call the draft-confirmation HTTP endpoint
                       yourself. The application handles confirmation
                       separately.
                    8. Do not treat "yes" or similar chat text as proof
                       that the application has confirmed the draft.
                    9. Never invent order IDs, order details, or statuses.

                    ORDER RETRIEVAL RULES:
                    Use the available order tools for actual database
                    information. Clearly report when no matching order
                    exists. Do not invent results.
                    """)
                .user(request.message())
                .tools(orderTools)
                .call()
                .content();

        return new ChatResponse(reply);
    }

}
