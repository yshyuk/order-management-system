package com.orderSystem.service;

import com.orderSystem.connector.DataConnectorInterface;
import com.orderSystem.domain.Order;
import com.orderSystem.exception.DataConnectorException;
import com.orderSystem.exception.DataTransformException;
import com.orderSystem.exception.OrderSyncException;
import com.orderSystem.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderSyncService {
    private static final Logger logger = LoggerFactory.getLogger(OrderSyncService.class);

    private final DataConnectorInterface connector;
    private final OrderRepository repository;
    private final DataTransformService transformer;

    @Autowired
    public OrderSyncService(DataConnectorInterface connector,
        OrderRepository repository,
        DataTransformService transformer) {
        this.connector = connector;
        this.repository = repository;
        this.transformer = transformer;
    }

    /**
     * 외부 시스템에서 주문 데이터를 가져와 저장합니다.
     * @param url 외부 시스템 URL
     * @throws OrderSyncException 동기화 실패 시 발생
     */
    public void syncOrdersFromExternal(String url) throws OrderSyncException {
        try {
            logger.info("외부 시스템에서 주문 데이터 동기화 시작: {}", url);

            String jsonData = connector.fetchData(url);
            logger.debug("받은 데이터: {}", jsonData);

            // 단일 주문 또는 주문 리스트 처리
            if (jsonData.trim().startsWith("[")) {
                // 배열 형태의 데이터
                List<Order> orders = transformer.jsonToOrderList(jsonData);
                for (Order order : orders) {
                    repository.save(order);
                    logger.info("주문 저장 완료: {}", order.getOrderId());
                }
                logger.info("총 {}개의 주문이 동기화되었습니다.", orders.size());
            } else {
                // 단일 객체 형태의 데이터
                Order order = transformer.jsonToOrder(jsonData);
                repository.save(order);
                logger.info("주문 저장 완료: {}", order.getOrderId());
            }

        } catch (DataConnectorException e) {
            logger.error("외부 시스템 연결 오류: {}", e.getMessage());
            throw new OrderSyncException("외부 시스템 연결 실패", e);
        } catch (DataTransformException e) {
            logger.error("데이터 변환 오류: {}", e.getMessage());
            throw new OrderSyncException("데이터 변환 실패", e);
        } catch (Exception e) {
            logger.error("예상치 못한 오류: {}", e.getMessage());
            throw new OrderSyncException("주문 동기화 실패", e);
        }
    }

    /**
     * 내부 주문 데이터를 외부 시스템으로 전송합니다.
     * @param url 외부 시스템 URL
     * @return 전송 성공 여부
     * @throws OrderSyncException 전송 실패 시 발생
     */
    public boolean sendOrdersToExternal(String url) throws OrderSyncException {
        try {
            logger.info("외부 시스템으로 주문 데이터 전송 시작: {}", url);

            List<Order> orders = repository.findAll();
            if (orders.isEmpty()) {
                logger.warn("전송할 주문 데이터가 없습니다.");
                return false;
            }

            String jsonData = transformer.orderListToJson(orders);
            logger.debug("전송할 데이터: {}", jsonData);

            boolean success = connector.sendData(url, jsonData);
            if (success) {
                logger.info("{}개의 주문 데이터 전송 완료", orders.size());
            } else {
                logger.error("주문 데이터 전송 실패");
            }

            return success;

        } catch (DataConnectorException e) {
            logger.error("외부 시스템 연결 오류: {}", e.getMessage());
            throw new OrderSyncException("외부 시스템 연결 실패", e);
        } catch (DataTransformException e) {
            logger.error("데이터 변환 오류: {}", e.getMessage());
            throw new OrderSyncException("데이터 변환 실패", e);
        } catch (Exception e) {
            logger.error("예상치 못한 오류: {}", e.getMessage());
            throw new OrderSyncException("주문 전송 실패", e);
        }
    }

    /**
     * 외부 시스템에서 단일 주문을 가져와 저장합니다.
     * @param url 외부 시스템 URL
     * @throws OrderSyncException 동기화 실패 시 발생
     */
    public void syncSingleOrder(String url) throws OrderSyncException {
        try {
            logger.info("외부 시스템에서 단일 주문 동기화 시작: {}", url);

            String jsonData = connector.fetchData(url);
            Order order = transformer.jsonToOrder(jsonData);
            repository.save(order);

            logger.info("주문 동기화 완료: {}", order.getOrderId());

        } catch (DataConnectorException e) {
            logger.error("외부 시스템 연결 오류: {}", e.getMessage());
            throw new OrderSyncException("외부 시스템 연결 실패", e);
        } catch (DataTransformException e) {
            logger.error("데이터 변환 오류: {}", e.getMessage());
            throw new OrderSyncException("데이터 변환 실패", e);
        }
    }
}