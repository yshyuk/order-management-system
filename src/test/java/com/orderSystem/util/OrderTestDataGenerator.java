package com.orderSystem.util;

import com.orderSystem.domain.Order;
import com.orderSystem.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderTestDataGenerator {

    private static final String[] CUSTOMER_NAMES = {
        "김철수", "이영희", "박민수", "최지은", "장동건",
        "윤서연", "임재현", "한소영", "조민호", "신예은"
    };

    private static final OrderStatus[] ORDER_STATUSES = OrderStatus.values();
    private static final Random random = new Random();

    /**
     * 지정된 개수의 랜덤 주문을 생성합니다.
     *
     * @param count 생성할 주문 개수
     * @return 생성된 주문 리스트
     */
    public static List<Order> generateRandomOrders(int count) {
        List<Order> orders = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String orderId = String.format("TEST-%04d", i);
            String customerName = CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)];
            LocalDateTime orderDate = LocalDateTime.now()
                .minusDays(random.nextInt(30))
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));
            OrderStatus status = ORDER_STATUSES[random.nextInt(ORDER_STATUSES.length)];

            orders.add(new Order(orderId, customerName, orderDate, status));
        }

        return orders;
    }

    /**
     * 특정 상태의 주문만 생성합니다.
     *
     * @param count  생성할 주문 개수
     * @param status 주문 상태
     * @return 생성된 주문 리스트
     */
    public static List<Order> generateOrdersWithStatus(int count, OrderStatus status) {
        List<Order> orders = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String orderId = String.format("%s-%04d", status.name(), i);
            String customerName = CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)];
            LocalDateTime orderDate = LocalDateTime.now()
                .minusHours(random.nextInt(72));

            orders.add(new Order(orderId, customerName, orderDate, status));
        }

        return orders;
    }
}