package com.example.agenticorderservice.controller;

import com.example.agenticorderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() throws Exception{
        String request = """
                {
                    "customerName": "Pratik",
                    "productName": "Mechanical Keyboard",
                    "quantity": 2,
                    "unitPrice": 2499.00
                }
                """;

        mockMvc.perform(
                post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Pratik"))
                .andExpect(jsonPath("$.productName").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldRejectInvalidOrder() throws Exception {
        String request = """
                {
                    "customerName": "",
                    "productName": "Mechanical Keyboard",
                    "quantity": 0,
                    "unitPrice": 2499.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.customerName").exists())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists());
    }

    @Test
    void shouldReturnNotFoundForMissingOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {
        String request = """
                {
                    "customerName": "Pratik",
                    "productName": "Mechanical Keyboard",
                    "quantity": 2,
                    "unitPrice": 2499.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Pratik"))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }


    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"customerName": "Pratik",
                             "quantity":
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void shouldRejectInvalidOrderId() throws Exception {
        mockMvc.perform(get("/api/orders/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
