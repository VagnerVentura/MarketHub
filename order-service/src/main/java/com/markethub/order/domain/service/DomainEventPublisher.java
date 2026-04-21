package com.markethub.order.domain.service;

import com.markethub.shared.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);

}
