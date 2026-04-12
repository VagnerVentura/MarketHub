package com.markethub.shared.exception;

public abstract class MarketHubException extends RuntimeException {
    private final String errorCode;

    protected MarketHubException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}