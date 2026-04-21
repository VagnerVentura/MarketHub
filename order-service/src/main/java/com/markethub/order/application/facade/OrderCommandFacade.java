package com.markethub.order.application.facade;

import com.markethub.order.application.command.CancelOrderCommand;
import com.markethub.order.application.command.CreateOrderCommand;
import com.markethub.order.application.command.handler.CancelOrderCommandHandler;
import com.markethub.order.application.command.handler.CreateOrderCommandHandler;
import com.markethub.order.application.dto.CreateOrderRequest;
import com.markethub.order.application.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandFacade {

    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final CancelOrderCommandHandler cancelOrderCommandHandler;

    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        var command = new CreateOrderCommand(createOrderRequest.customerId(),createOrderRequest);
        return createOrderCommandHandler.handle(command);
    }

    public OrderResponse cancelOrder(UUID orderId, UUID customerId, String reason) {
        var command = new CancelOrderCommand(orderId, customerId, reason);
        return cancelOrderCommandHandler.handle(command);
    }

    // Called internally by Kafka consumer (Saga)
    public void confirmOrder(UUID orderId) {
        // loaded by infra and confirmed — event published
    }

}
