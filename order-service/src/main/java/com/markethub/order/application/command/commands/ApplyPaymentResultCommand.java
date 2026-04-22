package com.markethub.order.application.command.commands;

import java.util.UUID;

public record ApplyPaymentResultCommand(
        UUID orderId,
        String paymentStatus,
        String reason
) {
}
