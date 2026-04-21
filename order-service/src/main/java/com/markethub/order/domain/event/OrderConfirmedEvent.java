package com.markethub.order.domain.event;

import com.markethub.shared.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID eventId,
        UUID aggregateId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() { return "OrderConfirmed"; }
}
