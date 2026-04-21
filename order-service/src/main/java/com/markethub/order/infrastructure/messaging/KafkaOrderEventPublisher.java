package com.markethub.order.infrastructure.messaging;

import com.markethub.order.domain.service.DomainEventPublisher;
import com.markethub.shared.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    @Override
    public void publish(DomainEvent event) {
        var record = new ProducerRecord<String, Object>(
                orderEventsTopic,
                event.aggregateId().toString(),
                event
        );

        kafkaTemplate.send(record).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event [{}] for aggregate [{}]: {}",
                        event.eventType(), event.aggregateId(), ex.getMessage(), ex);
            } else {
                log.info("Published event [{}] for aggregate [{}] → partition={} offset={}",
                        event.eventType(),
                        event.aggregateId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}