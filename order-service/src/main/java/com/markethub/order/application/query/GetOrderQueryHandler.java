package com.markethub.order.application.query;

import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.application.dto.GetOrderQuery;
import com.markethub.order.domain.repository.OrderRepository;
import com.markethub.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetOrderQueryHandler implements QueryHandler<GetOrderQuery, OrderResponse>{

    private final OrderRepository orderRepository;

    @Override
    @Cacheable(value = "orders", key = "#query.orderId()")
    public OrderResponse handle(GetOrderQuery query) {
        return orderRepository
                .findByIdAndCustomerId(query.orderId(), query.customerId())
                .map(OrderResponse::from)
                .orElseThrow(() -> new NotFoundException("Order", query.orderId()));
    }

}
