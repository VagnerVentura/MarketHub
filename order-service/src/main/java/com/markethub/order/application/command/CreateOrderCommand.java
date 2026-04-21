package com.markethub.order.application.command;

import com.markethub.order.application.dto.CreateOrderRequest;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(UUID customerId, CreateOrderRequest orderItems) {
}
