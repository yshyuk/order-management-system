package com.orderSystem.repository;

import com.orderSystem.domain.Order;
import com.orderSystem.exception.OrderNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepository {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    /**
     * 주문을 저장합니다.
     *
     * @param order 저장할 주문
     */
    public void save(Order order) {
        if (order == null || order.getOrderId() == null) {
            throw new IllegalArgumentException("주문 또는 주문 ID가 null입니다.");
        }
        orders.put(order.getOrderId(), order);
    }

    /**
     * 주문 ID로 주문을 조회합니다.
     *
     * @param orderId 주문 ID
     * @return 조회된 주문
     * @throws OrderNotFoundException 주문을 찾을 수 없는 경우
     */
    public Order findById(String orderId) throws OrderNotFoundException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new OrderNotFoundException("주문을 찾을 수 없습니다: " + orderId);
        }
        return order;
    }

    /**
     * 모든 주문을 조회합니다.
     *
     * @return 모든 주문 리스트
     */
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    /**
     * 주문을 삭제합니다.
     *
     * @param orderId 삭제할 주문 ID
     * @return 삭제 성공 여부
     */
    public boolean delete(String orderId) {
        return orders.remove(orderId) != null;
    }

    /**
     * 저장된 주문 수를 반환합니다.
     *
     * @return 주문 수
     */
    public int count() {
        return orders.size();
    }

    /**
     * 모든 주문을 삭제합니다.
     */
    public void clear() {
        orders.clear();
    }
}