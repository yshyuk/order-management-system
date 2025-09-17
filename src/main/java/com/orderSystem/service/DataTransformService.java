package com.orderSystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderSystem.domain.Order;
import com.orderSystem.exception.DataTransformException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataTransformService {

    private final ObjectMapper objectMapper;

    public DataTransformService() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    }

    /**
     * JSON 문자열을 Order 객체로 변환합니다.
     *
     * @param json JSON 문자열
     * @return Order 객체
     * @throws DataTransformException 변환 실패 시 발생
     */
    public Order jsonToOrder(String json) throws DataTransformException {
        try {
            if (json == null || json.trim().isEmpty()) {
                throw new DataTransformException("JSON 데이터가 비어있습니다.");
            }
            return objectMapper.readValue(json, Order.class);
        } catch (JsonProcessingException e) {
            throw new DataTransformException("JSON을 Order 객체로 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Order 객체를 JSON 문자열로 변환합니다.
     *
     * @param order Order 객체
     * @return JSON 문자열
     * @throws DataTransformException 변환 실패 시 발생
     */
    public String orderToJson(Order order) throws DataTransformException {
        try {
            if (order == null) {
                throw new DataTransformException("Order 객체가 null입니다.");
            }
            return objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new DataTransformException("Order를 JSON으로 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 문자열을 Order 리스트로 변환합니다.
     *
     * @param json JSON 문자열
     * @return Order 리스트
     * @throws DataTransformException 변환 실패 시 발생
     */
    public List<Order> jsonToOrderList(String json) throws DataTransformException {
        try {
            if (json == null || json.trim().isEmpty()) {
                throw new DataTransformException("JSON 데이터가 비어있습니다.");
            }
            return objectMapper.readValue(json, new TypeReference<List<Order>>() {
            });
        } catch (JsonProcessingException e) {
            throw new DataTransformException("JSON을 Order 리스트로 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Order 리스트를 JSON 문자열로 변환합니다.
     *
     * @param orders Order 리스트
     * @return JSON 문자열
     * @throws DataTransformException 변환 실패 시 발생
     */
    public String orderListToJson(List<Order> orders) throws DataTransformException {
        try {
            if (orders == null) {
                throw new DataTransformException("Order 리스트가 null입니다.");
            }
            return objectMapper.writeValueAsString(orders);
        } catch (JsonProcessingException e) {
            throw new DataTransformException("Order 리스트를 JSON으로 변환 실패: " + e.getMessage(), e);
        }
    }
}