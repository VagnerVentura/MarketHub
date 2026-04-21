package com.markethub.order.domain.event;

import com.markethub.shared.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId,
        UUID aggregateId,
        String reason,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() { return "OrderCancelled"; }
}
