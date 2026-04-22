package com.markethub.order.application.command.commands;

import com.markethub.order.application.dto.CreateOrderRequest;

import java.util.UUID;

public record CreateOrderCommand(UUID customerId, CreateOrderRequest orderItems) {
}
