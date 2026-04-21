package com.markethub.order.domain.exception;

import com.markethub.shared.exception.BusinessRuleException;

public class OrderStatusException extends BusinessRuleException {
    public OrderStatusException(String message) {
        super(message, "ORDER_INVALID_STATUS_TRANSITION");
    }
}
