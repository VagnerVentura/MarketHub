package com.markethub.order.application.dto;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record ListOrdersQuery(UUID customerId, Pageable pageable) {}