package com.markethub.order.domain.repository;

import com.markethub.order.domain.model.Order;
import com.markethub.order.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save (Order order);
    Optional<Order> findById(UUID id);
    Optional<Order> findByIdAndCustomerId(UUID id, UUID customerId);
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable );
    boolean existsById(UUID id);

}
