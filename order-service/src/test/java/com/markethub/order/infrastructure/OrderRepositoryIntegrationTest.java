package com.markethub.order.infrastructure;

import com.markethub.order.domain.model.Money;
import com.markethub.order.domain.model.Order;
import com.markethub.order.domain.model.OrderItem;
import com.markethub.order.domain.model.OrderStatus;
import com.markethub.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(com.markethub.order.infrastructure.persistence.OrderRepositoryImpl.class)
class OrderRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("orders_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndRetrieveOrder() {
        var customerId = UUID.randomUUID();
        var items      = List.of(buildItem());
        var order      = Order.create(customerId, items);

        var saved = orderRepository.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING);

        var found = orderRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(customerId);
        assertThat(found.get().getItems()).hasSize(1);
    }

    @Test
    void shouldFindOrdersByCustomer() {
        var customerId = UUID.randomUUID();

        // Create 3 orders for the same customer
        for (int i = 0; i < 3; i++) {
            orderRepository.save(Order.create(customerId, List.of(buildItem())));
        }
        // Create 1 order for a different customer
        orderRepository.save(Order.create(UUID.randomUUID(), List.of(buildItem())));

        var page = orderRepository.findByCustomerId(customerId, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void shouldUpdateOrderStatusOnConfirm() {
        var order = Order.create(UUID.randomUUID(), List.of(buildItem()));
        var saved = orderRepository.save(order);

        saved.confirm();
        var updated = orderRepository.save(saved);

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    private OrderItem buildItem() {
        return OrderItem.of(
                UUID.randomUUID(),
                "Notebook Pro",
                2,
                Money.of(new BigDecimal("1999.99"), "BRL")
        );
    }
}
