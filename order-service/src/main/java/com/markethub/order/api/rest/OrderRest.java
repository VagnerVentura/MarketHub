package com.markethub.order.api.rest;

import com.markethub.order.application.dto.CreateOrderRequest;
import com.markethub.order.application.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/orders")
@Tag(name = "Orders")
public interface OrderRest {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Order")
    ResponseEntity<OrderResponse> createOrder (@RequestHeader("X-User-Id") UUID customerId,@Valid @RequestBody CreateOrderRequest request);

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Order By ID")
    ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId,@RequestHeader("X-User-Id") UUID customerId);

    @GetMapping
    @Operation(summary = "List orders for authenticated customer")
    ResponseEntity<Page<OrderResponse>> listOrders(@RequestHeader("X-User-Id") UUID customerId,@PageableDefault(size = 20, sort = "createdAt") Pageable pageable);

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel an order")
    ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId,@RequestHeader("X-User-Id") UUID customerId,@RequestParam(defaultValue = "Cancelled by customer") String reason);
}
