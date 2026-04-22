package com.markethub.order.application.command.handler;

import com.markethub.order.application.command.commands.ApplyPaymentResultCommand;
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
public class ApplyPaymentResultCommandHandler {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    public void handle(ApplyPaymentResultCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new NotFoundException("Order", command.orderId()));

        if ("APPROVED".equalsIgnoreCase(command.paymentStatus())) {
            order.confirm();
        } else {
            String reason = command.reason() == null || command.reason().isBlank()
                    ? "Payment declined"
                    : command.reason();
            order.cancel(reason);
        }

        var saved = orderRepository.save(order);
        eventPublisher.publishAll(saved.getDomainEvents());
        saved.clearDomainEvents();

        log.info("Applied payment result to order {} with status {}", command.orderId(), command.paymentStatus());
    }
}
