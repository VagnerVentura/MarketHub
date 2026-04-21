package com.markethub.order.application.dto;

import com.markethub.order.domain.enums.OrderStatus;
import com.markethub.order.domain.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}