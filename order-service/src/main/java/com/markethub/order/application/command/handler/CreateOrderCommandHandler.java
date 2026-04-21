package com.markethub.order.application.command.handler;

import com.markethub.order.application.command.CommandHandler;
import com.markethub.order.application.command.CreateOrderCommand;
import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.domain.exception.InsufficientStockException;
import com.markethub.order.domain.model.Money;
import com.markethub.order.domain.model.Order;
import com.markethub.order.domain.model.OrderItem;
import com.markethub.order.domain.repository.OrderRepository;
import com.markethub.order.domain.service.DomainEventPublisher;
import com.markethub.order.infrastructure.client.ProductServiceClient;
import com.markethub.order.infrastructure.client.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand, OrderResponse> {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final DomainEventPublisher eventPublisher;

    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    @Override
    public OrderResponse handle(CreateOrderCommand command){
        //criando
        log.info("Creating order for customer: {}", command.customerId());

        //busca items advindos do comando e builda
        List<OrderItem> items = command.orderItems().items().stream()
                .map(req -> buildOrderItem(req.productId(), req.quantity()))
                .toList();

        Order order = Order.create(command.customerId(), items);
        Order saved = orderRepository.save(order);

        eventPublisher.publishAll(saved.getDomainEvents());
        saved.clearDomainEvents();

        log.info("Order created with id: {}", saved.getId());
        return OrderResponse.from(saved);
    }

    private OrderItem buildOrderItem(UUID productId, int quantity){
        ProductResponse product = productServiceClient.getProduct(productId);

        if(product.stock() < quantity){
            throw new InsufficientStockException(productId);
        }

        return OrderItem.of(
                productId,
                product.name(),
                quantity,
                Money.of(product.price(), product.currency())
        );
    }

    public OrderResponse fallback(CreateOrderCommand command, Exception ex){
        log.error("Circuit breaker open for product-service: {}", ex.getMessage());
        throw new RuntimeException("Product service is temporarily unavailable. Please try again later.");
    }
}