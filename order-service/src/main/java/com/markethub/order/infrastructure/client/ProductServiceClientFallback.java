package com.markethub.order.infrastructure.client;

import com.markethub.order.infrastructure.client.dto.ProductResponse;
import com.markethub.order.infrastructure.client.ProductServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ProductServiceClientFallback implements ProductServiceClient {

    @Override
    public ProductResponse getProduct(UUID productId) {
        log.error("Fallback triggered for product-service. Product id: {}", productId);
        throw new RuntimeException("Product service is currently unavailable. Please try again later.");
    }
}