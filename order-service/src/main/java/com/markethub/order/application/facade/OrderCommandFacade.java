package com.markethub.order.application.facade;

import com.markethub.order.application.command.commands.CancelOrderCommand;
import com.markethub.order.application.command.commands.CreateOrderCommand;
import com.markethub.order.application.command.handler.CancelOrderCommandHandler;
import com.markethub.order.application.command.handler.CreateOrderCommandHandler;
import com.markethub.order.application.command.commands.ApplyPaymentResultCommand;
import com.markethub.order.application.command.handler.ApplyPaymentResultCommandHandler;
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
    private final ApplyPaymentResultCommandHandler applyPaymentResultCommandHandler;

    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        var command = new CreateOrderCommand(createOrderRequest.customerId(), createOrderRequest);
        return createOrderCommandHandler.handle(command);
    }

    public OrderResponse cancelOrder(UUID orderId, UUID customerId, String reason) {
        var command = new CancelOrderCommand(orderId, customerId, reason);
        return cancelOrderCommandHandler.handle(command);
    }

    public void applyPaymentResult(UUID orderId, String paymentStatus, String reason) {
        var command = new ApplyPaymentResultCommand(orderId, paymentStatus, reason);
        applyPaymentResultCommandHandler.handle(command);
    }
}
