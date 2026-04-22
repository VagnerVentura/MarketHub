package com.markethub.order.infrastructure.messaging;

import com.markethub.order.application.facade.OrderCommandFacade;
import com.markethub.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderCommandFacade orderCommandFacade;

    @KafkaListener(
            topics = "${kafka.topics.payment-events}",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handlePaymentEvent(@Payload Map<String, Object> payload, Acknowledgment ack) {
        try {
            String status = (String) payload.get("status");
            UUID orderId = UUID.fromString((String) payload.get("orderId"));
            String reason = (String) payload.get("reason");

            log.info("Received payment event: status={} orderId={}", status, orderId);
            orderCommandFacade.applyPaymentResult(orderId, status, reason);

            ack.acknowledge();
        } catch (NotFoundException ex) {
            log.error("Order not found during payment event processing: {}", ex.getMessage());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing payment event - will retry: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
