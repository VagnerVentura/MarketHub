package com.markethub.order.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",   column = @Column(name = "unit_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money unitPrice;

    public static OrderItem of(UUID productId, String productName, int quantity, Money unitPrice) {
        var item = new OrderItem();
        item.productId   = productId;
        item.productName = productName;
        item.quantity    = quantity;
        item.unitPrice   = unitPrice;
        return item;
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }
}