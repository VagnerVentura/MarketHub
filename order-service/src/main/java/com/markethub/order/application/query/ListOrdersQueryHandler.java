package com.markethub.order.application.query;

import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.application.dto.ListOrdersQuery;
import com.markethub.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListOrdersQueryHandler implements QueryHandler<ListOrdersQuery, Page<OrderResponse>> {

    private final OrderRepository orderRepository;

    @Override
    public Page<OrderResponse> handle(ListOrdersQuery query) {
        return orderRepository
                .findByCustomerId(query.customerId(), query.pageable())
                .map(OrderResponse::from);
    }
}