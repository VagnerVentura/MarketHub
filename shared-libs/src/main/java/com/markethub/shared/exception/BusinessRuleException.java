package com.markethub.shared.exception;

public class BusinessRuleException extends MarketHubException {
    public BusinessRuleException(String message, String errorCode) {
        super(message, errorCode);
    }
}