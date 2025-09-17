package com.orderSystem.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        order = new Order("ORD-001", "홍길동", testDate, OrderStatus.PROCESSING);
    }

    @Test
    void testOrderCreation() {
        assertNotNull(order);
        assertEquals("ORD-001", order.getOrderId());
        assertEquals("홍길동", order.getCustomerName());
        assertEquals(testDate, order.getOrderDate());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @Test
    void testJsonSerialization() throws JsonProcessingException {
        String json = order.toJson();
        assertNotNull(json);
        assertTrue(json.contains("ORD-001"));
        assertTrue(json.contains("홍길동"));
        assertTrue(json.contains("PROCESSING"));
    }

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        String json = "{\"orderId\":\"ORD-002\",\"customerName\":\"김영희\"," +
            "\"orderDate\":\"2024-01-16T14:30:00\",\"status\":\"SHIPPING\"}";

        Order deserializedOrder = Order.fromJson(json);

        assertNotNull(deserializedOrder);
        assertEquals("ORD-002", deserializedOrder.getOrderId());
        assertEquals("김영희", deserializedOrder.getCustomerName());
        assertEquals(OrderStatus.SHIPPING, deserializedOrder.getStatus());
    }

    @Test
    void testEquals() {
        Order sameOrder = new Order("ORD-001", "다른이름", testDate, OrderStatus.COMPLETED);
        Order differentOrder = new Order("ORD-002", "홍길동", testDate, OrderStatus.PROCESSING);

        assertEquals(order, sameOrder); // 주문 ID가 같으면 동일
        assertNotEquals(order, differentOrder); // 주문 ID가 다르면 다름
    }
}
