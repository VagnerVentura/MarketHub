package com.markethub.order.domain.event;

import com.markethub.order.domain.model.Money;
import com.markethub.shared.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID aggregateId,
        UUID customerId,
        Money totalAmount,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String eventType() {
        return "OrderCreated";
    }
}
