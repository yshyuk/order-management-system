package com.orderSystem.service;

import com.orderSystem.domain.Order;
import com.orderSystem.domain.OrderStatus;
import com.orderSystem.exception.DataTransformException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataTransformServiceTest {

    private DataTransformService service;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        service = new DataTransformService();
        testOrder = new Order("ORD-001", "홍길동",
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            OrderStatus.PROCESSING);
    }

    @Test
    void testOrderToJson() throws DataTransformException {
        String json = service.orderToJson(testOrder);

        assertNotNull(json);
        assertTrue(json.contains("ORD-001"));
        assertTrue(json.contains("홍길동"));
        assertTrue(json.contains("PROCESSING"));
        assertTrue(json.contains("2024-01-15T10:30:00"));
    }

    @Test
    void testJsonToOrder() throws DataTransformException {
        String json = "{\"orderId\":\"ORD-002\",\"customerName\":\"김영희\"," +
            "\"orderDate\":\"2024-01-16T14:30:00\",\"status\":\"SHIPPING\"}";

        Order order = service.jsonToOrder(json);

        assertNotNull(order);
        assertEquals("ORD-002", order.getOrderId());
        assertEquals("김영희", order.getCustomerName());
        assertEquals(OrderStatus.SHIPPING, order.getStatus());
    }

    @Test
    void testOrderListToJson() throws DataTransformException {
        Order order2 = new Order("ORD-002", "김영희",
            LocalDateTime.of(2024, 1, 16, 14, 30, 0),
            OrderStatus.SHIPPING);
        List<Order> orders = Arrays.asList(testOrder, order2);

        String json = service.orderListToJson(orders);

        assertNotNull(json);
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        assertTrue(json.contains("ORD-001"));
        assertTrue(json.contains("ORD-002"));
    }

    @Test
    void testJsonToOrderList() throws DataTransformException {
        String json = "[{\"orderId\":\"ORD-001\",\"customerName\":\"홍길동\"," +
            "\"orderDate\":\"2024-01-15T10:30:00\",\"status\":\"PROCESSING\"}," +
            "{\"orderId\":\"ORD-002\",\"customerName\":\"김영희\"," +
            "\"orderDate\":\"2024-01-16T14:30:00\",\"status\":\"SHIPPING\"}]";

        List<Order> orders = service.jsonToOrderList(json);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals("ORD-001", orders.get(0).getOrderId());
        assertEquals("ORD-002", orders.get(1).getOrderId());
    }

    @Test
    void testJsonToOrderWithInvalidJson() {
        assertThrows(DataTransformException.class, () -> {
            service.jsonToOrder("invalid json");
        });
    }

    @Test
    void testOrderToJsonWithNullOrder() {
        assertThrows(DataTransformException.class, () -> {
            service.orderToJson(null);
        });
    }

    @Test
    void testJsonToOrderWithEmptyString() {
        assertThrows(DataTransformException.class, () -> {
            service.jsonToOrder("");
        });
    }
}