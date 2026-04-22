package com.markethub.order.application.command.handler;

import com.markethub.order.application.command.commands.CancelOrderCommand;
import com.markethub.order.application.command.commands.CommandHandler;
import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.domain.repository.OrderRepository;
import com.markethub.order.domain.service.DomainEventPublisher;
import com.markethub.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CancelOrderCommandHandler implements CommandHandler<CancelOrderCommand, OrderResponse> {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    public OrderResponse handle(CancelOrderCommand command) {
        var order = orderRepository
                .findByIdAndCustomerId(command.orderId(), command.customerId())
                .orElseThrow(() -> new NotFoundException("Order ", command.orderId()));

        order.cancel(command.reason());

        var saved = orderRepository.save(order);
        eventPublisher.publishAll(saved.getDomainEvents());
        saved.clearDomainEvents();

        log.info("Order {} cancelled. Reason: {}", command.orderId(), command.reason());
        return OrderResponse.from(saved);
    }
}
