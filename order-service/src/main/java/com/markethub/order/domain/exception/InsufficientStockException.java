package com.markethub.order.domain.exception;

import com.markethub.shared.exception.BusinessRuleException;

import java.util.UUID;

public class InsufficientStockException extends BusinessRuleException {
    public InsufficientStockException(UUID productId) {
        super("Insufficient stock for product: " + productId, "INSUFFICIENT_STOCK");
    }
}

