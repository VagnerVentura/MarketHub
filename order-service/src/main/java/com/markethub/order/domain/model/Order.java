package com.markethub.order.domain.model;

import com.markethub.order.domain.event.OrderCancelledEvent;
import com.markethub.order.domain.event.OrderConfirmedEvent;
import com.markethub.order.domain.event.OrderCreatedEvent;
import com.markethub.order.domain.exception.OrderStatusException;
import com.markethub.order.domain.enums.OrderStatus;
import com.markethub.shared.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Embedded
    private Money totalAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    // -------------------------------------------------------------------------
    // Factory Method (DDD pattern — never use new Order() from outside)
    // -------------------------------------------------------------------------
    public static Order create(UUID customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        var order        = new Order();
        order.customerId = customerId;
        order.status     = OrderStatus.PENDING;
        order.items      = new ArrayList<>(items);
        order.totalAmount = items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
        order.createdAt  = Instant.now();
        order.updatedAt  = Instant.now();

        order.addDomainEvent(new OrderCreatedEvent(
                UUID.randomUUID(), order.id, customerId, order.totalAmount, Instant.now()
        ));

        return order;
    }

    // -------------------------------------------------------------------------
    // Business behaviour
    // -------------------------------------------------------------------------
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new OrderStatusException("Order can only be confirmed when PENDING, current: " + status);
        }
        this.status    = OrderStatus.CONFIRMED;
        this.updatedAt = Instant.now();
        addDomainEvent(new OrderConfirmedEvent(UUID.randomUUID(), this.id, Instant.now()));
    }

    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.REFUNDED) {
            throw new OrderStatusException("Cannot cancel order in status: " + status);
        }
        this.status    = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
        addDomainEvent(new OrderCancelledEvent(UUID.randomUUID(), this.id, reason, Instant.now()));
    }

    public void markAsProcessing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new OrderStatusException("Order must be CONFIRMED to start processing");
        }
        this.status    = OrderStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markAsShipped() {
        if (status != OrderStatus.PROCESSING) {
            throw new OrderStatusException("Order must be PROCESSING to be shipped");
        }
        this.status    = OrderStatus.SHIPPED;
        this.updatedAt = Instant.now();
    }

    public void markAsDelivered() {
        if (status != OrderStatus.SHIPPED) {
            throw new OrderStatusException("Order must be SHIPPED to be delivered");
        }
        this.status    = OrderStatus.DELIVERED;
        this.updatedAt = Instant.now();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = Instant.now();
    }
}