package com.orderSystem.service;

import com.orderSystem.connector.MockHttpDataConnector;
import com.orderSystem.domain.Order;
import com.orderSystem.domain.OrderStatus;
import com.orderSystem.exception.OrderSyncException;
import com.orderSystem.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderSyncServiceTest {
    private OrderSyncService syncService;
    private MockHttpDataConnector mockConnector;
    private OrderRepository repository;
    private DataTransformService transformer;

    @BeforeEach
    void setUp() {
        mockConnector = new MockHttpDataConnector();
        repository = new OrderRepository();
        transformer = new DataTransformService();
        syncService = new OrderSyncService(mockConnector, repository, transformer);
    }

    @Test
    void testSyncSingleOrderFromExternal() throws Exception {
        String mockJson = "{\"orderId\":\"ORD-001\",\"customerName\":\"홍길동\"," +
            "\"orderDate\":\"2024-01-15T10:30:00\",\"status\":\"PROCESSING\"}";
        mockConnector.setMockResponse(mockJson);

        syncService.syncSingleOrder("http://external-api.com/order/1");

        assertEquals(1, repository.count());
        Order savedOrder = repository.findById("ORD-001");
        assertEquals("홍길동", savedOrder.getCustomerName());
        assertEquals(OrderStatus.PROCESSING, savedOrder.getStatus());
    }

    @Test
    void testSyncMultipleOrdersFromExternal() throws Exception {
        String mockJson = "[" +
            "{\"orderId\":\"ORD-001\",\"customerName\":\"홍길동\"," +
            "\"orderDate\":\"2024-01-15T10:30:00\",\"status\":\"PROCESSING\"}," +
            "{\"orderId\":\"ORD-002\",\"customerName\":\"김영희\"," +
            "\"orderDate\":\"2024-01-16T14:30:00\",\"status\":\"SHIPPING\"}" +
            "]";
        mockConnector.setMockResponse(mockJson);

        syncService.syncOrdersFromExternal("http://external-api.com/orders");

        assertEquals(2, repository.count());
        Order order1 = repository.findById("ORD-001");
        Order order2 = repository.findById("ORD-002");
        assertNotNull(order1);
        assertNotNull(order2);
    }

    @Test
    void testSendOrdersToExternal() throws Exception {
        Order order1 = new Order("ORD-001", "홍길동",
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            OrderStatus.PROCESSING);
        Order order2 = new Order("ORD-002", "김영희",
            LocalDateTime.of(2024, 1, 16, 14, 30, 0),
            OrderStatus.SHIPPING);

        repository.save(order1);
        repository.save(order2);

        boolean result = syncService.sendOrdersToExternal("http://external-api.com/orders");

        assertTrue(result);
    }

    @Test
    void testSyncFailureHandling() {
        mockConnector.setFailure("Network timeout");

        assertThrows(OrderSyncException.class, () -> {
            syncService.syncSingleOrder("http://external-api.com/order/1");
        });
    }

    @Test
    void testSendEmptyOrderList() throws Exception {
        boolean result = syncService.sendOrdersToExternal("http://external-api.com/orders");
        assertFalse(result); // 빈 목록은 전송하지 않음
    }
}