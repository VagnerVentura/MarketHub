package com.markethub.order.application.command.commands;

import java.util.UUID;

public record CancelOrderCommand(UUID orderId, UUID customerId, String reason) {
}
