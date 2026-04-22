package com.markethub.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markethub.order.api.controller.OrderController;
import com.markethub.order.application.OrderApplicationService;
import com.markethub.order.application.dto.CreateOrderRequest;
import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  OrderApplicationService orderService;

    private final UUID customerId = UUID.randomUUID();
    private final UUID orderId    = UUID.randomUUID();
    private final UUID productId  = UUID.randomUUID();

    @Test
    void shouldCreateOrderAndReturn201() throws Exception {
        var request  = new CreateOrderRequest(customerId, List.of(
                new CreateOrderRequest.ItemRequest(productId, 2)));
        var response = buildOrderResponse();

        when(orderService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", customerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(3999.98));
    }

    @Test
    void shouldReturn400WhenItemsIsEmpty() throws Exception {
        var request = new CreateOrderRequest(customerId, List.of());

        mockMvc.perform(post("/api/v1/orders")
                        .header("X-User-Id", customerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        when(orderService.getOrder(orderId, customerId)).thenReturn(buildOrderResponse());

        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .header("X-User-Id", customerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    private OrderResponse buildOrderResponse() {
        return new OrderResponse(
                orderId, customerId, OrderStatus.PENDING,
                new BigDecimal("3999.98"), "BRL",
                List.of(new OrderResponse.OrderItemResponse(
                        productId, "Notebook Pro", 2,
                        new BigDecimal("1999.99"), new BigDecimal("3999.98"))),
                Instant.now(), Instant.now()
        );
    }
}
