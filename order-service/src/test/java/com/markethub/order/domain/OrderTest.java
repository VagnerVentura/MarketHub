package com.markethub.order.domain;

import com.markethub.order.domain.event.OrderCancelledEvent;
import com.markethub.order.domain.event.OrderConfirmedEvent;
import com.markethub.order.domain.event.OrderCreatedEvent;
import com.markethub.order.domain.exception.OrderStatusException;
import com.markethub.order.domain.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithPendingStatusAndDomainEvent() {
        var items = List.of(item(BigDecimal.TEN, 2));
        var order = Order.create(UUID.randomUUID(), items);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("20.00");
        assertThat(order.getDomainEvents()).hasSize(1);
        assertThat(order.getDomainEvents().get(0)).isInstanceOf(OrderCreatedEvent.class);
    }

    @Test
    void shouldNotCreateOrderWithEmptyItems() {
        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    void shouldConfirmPendingOrder() {
        var order = pendingOrder();
        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getDomainEvents()).anyMatch(e -> e instanceof OrderConfirmedEvent);
    }

    @Test
    void shouldNotConfirmAlreadyConfirmedOrder() {
        var order = pendingOrder();
        order.confirm();

        assertThatThrownBy(order::confirm)
                .isInstanceOf(OrderStatusException.class);
    }

    @Test
    void shouldCancelPendingOrder() {
        var order = pendingOrder();
        order.cancel("Changed my mind");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getDomainEvents()).anyMatch(e -> e instanceof OrderCancelledEvent);
    }

    @Test
    void shouldNotCancelDeliveredOrder() {
        var order = pendingOrder();
        order.confirm();
        order.markAsProcessing();
        order.markAsShipped();
        order.markAsDelivered();

        assertThatThrownBy(() -> order.cancel("too late"))
                .isInstanceOf(OrderStatusException.class)
                .hasMessageContaining("DELIVERED");
    }

    @Test
    void shouldCalculateTotalCorrectly() {
        var items = List.of(
                item(new BigDecimal("10.00"), 3),  // 30.00
                item(new BigDecimal("5.50"), 2)    // 11.00
        );
        var order = Order.create(UUID.randomUUID(), items);

        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("41.00");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private Order pendingOrder() {
        return Order.create(UUID.randomUUID(), List.of(item(BigDecimal.TEN, 1)));
    }

    private OrderItem item(BigDecimal price, int qty) {
        return OrderItem.of(UUID.randomUUID(), "Product", qty, Money.of(price, "BRL"));
    }
}
