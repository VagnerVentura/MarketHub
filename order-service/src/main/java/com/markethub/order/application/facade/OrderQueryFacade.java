package com.markethub.order.application.facade;

import com.markethub.order.application.dto.GetOrderQuery;
import com.markethub.order.application.dto.ListOrdersQuery;
import com.markethub.order.application.dto.OrderResponse;
import com.markethub.order.application.query.GetOrderQueryHandler;
import com.markethub.order.application.query.ListOrdersQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderQueryFacade {

    private final GetOrderQueryHandler getOrderQueryHandler;
    private final ListOrdersQueryHandler listOrdersQueryHandler;

    public OrderResponse getOrder(UUID orderId, UUID customerId) {
        var query = new GetOrderQuery(orderId,customerId);
        return getOrderQueryHandler.handle(query);
    }

    public Page<OrderResponse> listOrders(UUID customerId, Pageable pageable) {
        var query = new ListOrdersQuery(customerId,pageable);
        return listOrdersQueryHandler.handle(query);
    }

}
