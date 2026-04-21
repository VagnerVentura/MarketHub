package com.markethub.order.application.dto;

import java.util.UUID;

// ─── Get single order ─────────────────────────────────────────────────────────
public record GetOrderQuery(UUID orderId, UUID customerId) {
}
