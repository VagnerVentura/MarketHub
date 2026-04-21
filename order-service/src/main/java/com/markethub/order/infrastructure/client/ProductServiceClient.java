package com.markethub.order.infrastructure.client;

import com.markethub.order.infrastructure.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "product-service",
        url = "${services.product-service.url:http://localhost:8082}",
        fallback = ProductServiceClientFallback.class
)
public interface ProductServiceClient {

    @GetMapping("/api/v1/products/{productId}")
    ProductResponse getProduct(@PathVariable UUID productId);

}
