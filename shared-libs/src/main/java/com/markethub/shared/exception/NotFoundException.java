package com.markethub.shared.exception;

public class NotFoundException extends MarketHubException {
    public NotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id, "RESOURCE_NOT_FOUND");
    }
}
