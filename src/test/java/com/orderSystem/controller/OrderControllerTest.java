package com.orderSystem.controller;

import com.orderSystem.domain.Order;
import com.orderSystem.domain.OrderStatus;
import com.orderSystem.repository.OrderRepository;
import com.orderSystem.service.OrderSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class OrderControllerTestWithMockMvc {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        // MockMvc 수동 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        orderRepository.clear();
        testOrder = new Order("ORD-001", "홍길동",
            LocalDateTime.of(2024, 1, 15, 10, 30, 0),
            OrderStatus.PROCESSING);
    }

    @Test
    void testGetAllOrdersEmpty() throws Exception {
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetAllOrdersWithData() throws Exception {
        orderRepository.save(testOrder);

        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].orderId").value("ORD-001"))
            .andExpect(jsonPath("$.data[0].customerName").value("홍길동"));
    }

    @Test
    void testGetOrderById() throws Exception {
        orderRepository.save(testOrder);

        mockMvc.perform(get("/api/orders/ORD-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.orderId").value("ORD-001"))
            .andExpect(jsonPath("$.data.customerName").value("홍길동"));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/NON-EXISTENT"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetOrderCount() throws Exception {
        orderRepository.save(testOrder);

        mockMvc.perform(get("/api/orders/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void testClearAllOrders() throws Exception {
        orderRepository.save(testOrder);

        mockMvc.perform(delete("/api/orders/clear"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
}