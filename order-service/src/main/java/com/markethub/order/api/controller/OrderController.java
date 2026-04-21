package com.markethub.order.api.controller;

import com.markethub.order.api.rest.OrderRest;
import com.markethub.order.application.facade.OrderCommandFacade;
import com.markethub.order.application.dto.CreateOrderRequest;
import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.application.facade.OrderQueryFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderRest {

    private final OrderCommandFacade orderCommandFacade;
    private final OrderQueryFacade orderQueryFacade;

    @Override
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("X-User-Id") UUID customerId,
            @Valid @RequestBody CreateOrderRequest request)
    {
        var req = new CreateOrderRequest(customerId, request.items()); // substitui o customerId do body pelo header validado pelo gateway
        return ResponseEntity.status(HttpStatus.CREATED).body(orderCommandFacade.createOrder(req));
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(UUID orderId, UUID customerId) {
        return ResponseEntity.ok(orderQueryFacade.getOrder(orderId, customerId));
    }

    @Override
    public ResponseEntity<Page<OrderResponse>> listOrders(UUID customerId, Pageable pageable) {
        return ResponseEntity.ok(orderQueryFacade.listOrders(customerId, pageable));
    }

    @Override
    public ResponseEntity<OrderResponse> cancelOrder(UUID orderId, UUID customerId, String reason) {
        return ResponseEntity.ok(orderCommandFacade.cancelOrder(orderId, customerId, reason));
    }

}
