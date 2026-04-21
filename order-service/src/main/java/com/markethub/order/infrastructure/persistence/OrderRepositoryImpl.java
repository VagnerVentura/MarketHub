package com.markethub.order.infrastructure.persistence;

import com.markethub.order.domain.enums.OrderStatus;
import com.markethub.order.domain.model.Order;
import com.markethub.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderJpaRepository.findByIdWithItems(id);
    }

    @Override
    public Optional<Order> findByIdAndCustomerId(UUID id, UUID customerId) {
        return orderJpaRepository.findByIdAndCustomerId(id,customerId);
    }

    @Override
    public Page<Order> findByCustomerId(UUID customerId, Pageable pageable) {
        return orderJpaRepository.findByCustomerId(customerId,pageable);
    }

    @Override
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return orderJpaRepository.findByStatus(status,pageable);
    }

    @Override
    public boolean existsById(UUID id) {
        return orderJpaRepository.existsById(id);
    }
}
