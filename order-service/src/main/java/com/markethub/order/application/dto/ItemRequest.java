package com.markethub.order.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemRequest(
        @NotNull
        UUID productId,

        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Quantity must not exceed 100")
        int quantity
) {}


