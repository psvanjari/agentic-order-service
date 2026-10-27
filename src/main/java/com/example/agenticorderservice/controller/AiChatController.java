package com.example.agenticorderservice.controller;

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

    public AiChatController(ChatClient.Builder chatClientBuilder){
        this.chatClient=chatClientBuilder.build();
    }

    @PostMapping(
            value = "/chat",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ChatResponse chat(@Valid @RequestBody ChatRequest request){
        String answer=chatClient.prompt()
                .user(request.message())
                .call()
                .content();

        return new ChatResponse(answer);
    }
}
