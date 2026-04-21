package com.markethub.order.application.command;

import java.util.UUID;

public record CancelOrderCommand(UUID orderId, UUID customerId, String reason) {
}
