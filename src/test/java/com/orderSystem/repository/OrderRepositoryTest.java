package com.orderSystem.repository;

import com.orderSystem.domain.Order;
import com.orderSystem.domain.OrderStatus;
import com.orderSystem.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    private OrderRepository repository;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        repository = new OrderRepository();
        testOrder = new Order("ORD-001", "홍길동",
            LocalDateTime.now(), OrderStatus.PROCESSING);
    }

    @Test
    void testSaveAndFindById() throws OrderNotFoundException {
        repository.save(testOrder);

        Order foundOrder = repository.findById("ORD-001");
        assertNotNull(foundOrder);
        assertEquals(testOrder.getOrderId(), foundOrder.getOrderId());
        assertEquals(testOrder.getCustomerName(), foundOrder.getCustomerName());
    }

    @Test
    void testFindByIdNotFound() {
        assertThrows(OrderNotFoundException.class, () -> {
            repository.findById("NON-EXISTENT");
        });
    }

    @Test
    void testFindAll() {
        repository.save(testOrder);
        repository.save(new Order("ORD-002", "김영희",
            LocalDateTime.now(), OrderStatus.SHIPPING));

        List<Order> orders = repository.findAll();
        assertEquals(2, orders.size());
    }

    @Test
    void testDelete() {
        repository.save(testOrder);
        assertEquals(1, repository.count());

        boolean deleted = repository.delete("ORD-001");
        assertTrue(deleted);
        assertEquals(0, repository.count());

        boolean deletedAgain = repository.delete("ORD-001");
        assertFalse(deletedAgain);
    }

    @Test
    void testClear() {
        repository.save(testOrder);
        repository.save(new Order("ORD-002", "김영희",
            LocalDateTime.now(), OrderStatus.SHIPPING));
        assertEquals(2, repository.count());

        repository.clear();
        assertEquals(0, repository.count());
    }

    @Test
    void testSaveNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    void testSaveOrderWithNullId() {
        Order orderWithNullId = new Order(null, "홍길동",
            LocalDateTime.now(), OrderStatus.PROCESSING);
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(orderWithNullId);
        });
    }
}
