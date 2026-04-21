package com.markethub.order.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        String currency,
        int stock,
        boolean active
) {}