package com.orderSystem.controller;

import com.orderSystem.domain.Order;
import com.orderSystem.dto.ApiResponse;
import com.orderSystem.dto.ErrorResponse;
import com.orderSystem.exception.OrderNotFoundException;
import com.orderSystem.exception.OrderSyncException;
import com.orderSystem.repository.OrderRepository;
import com.orderSystem.service.OrderSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderSyncService orderSyncService;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderSyncService orderSyncService, OrderRepository orderRepository) {
        this.orderSyncService = orderSyncService;
        this.orderRepository = orderRepository;
    }

    /**
     * 모든 주문을 조회합니다.
     *
     * @return 모든 주문 리스트
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            logger.info("전체 주문 조회 완료: {}개", orders.size());
            return ResponseEntity.ok(ApiResponse.success("주문 목록 조회 완료", orders));
        } catch (Exception e) {
            logger.error("주문 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("주문 목록 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 주문을 조회합니다.
     *
     * @param orderId 주문 ID
     * @return 조회된 주문
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable String orderId) {
        try {
            Order order = orderRepository.findById(orderId);
            logger.info("주문 조회 완료: {}", orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 조회 완료", order));
        } catch (OrderNotFoundException e) {
            logger.warn("주문 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(e.getMessage()));
        } catch (Exception e) {
            logger.error("주문 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("주문 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 외부 시스템에서 주문 데이터를 가져와 동기화합니다.
     *
     * @param externalUrl 외부 시스템 URL
     * @return 동기화 결과
     */
    @PostMapping("/sync-from")
    public ResponseEntity<ApiResponse<String>> syncFromExternal(@RequestParam String externalUrl) {
        try {
            if (externalUrl == null || externalUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("외부 시스템 URL이 필요합니다."));
            }

            int beforeCount = orderRepository.count();
            orderSyncService.syncOrdersFromExternal(externalUrl);
            int afterCount = orderRepository.count();
            int syncedCount = afterCount - beforeCount;

            String message = String.format("외부 시스템에서 %d개의 주문을 성공적으로 동기화했습니다.", syncedCount);
            logger.info(message);
            return ResponseEntity.ok(ApiResponse.success(message, externalUrl));

        } catch (OrderSyncException e) {
            logger.error("주문 동기화 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("동기화 실패: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("예상치 못한 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("시스템 오류: " + e.getMessage()));
        }
    }

    /**
     * 내부 주문 데이터를 외부 시스템으로 전송합니다.
     *
     * @param externalUrl 외부 시스템 URL
     * @return 전송 결과
     */
    @PostMapping("/sync-to")
    public ResponseEntity<ApiResponse<String>> sendToExternal(@RequestParam String externalUrl) {
        try {
            if (externalUrl == null || externalUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("외부 시스템 URL이 필요합니다."));
            }

            boolean success = orderSyncService.sendOrdersToExternal(externalUrl);

            if (success) {
                int orderCount = orderRepository.count();
                String message = String.format("총 %d개의 주문을 외부 시스템으로 성공적으로 전송했습니다.", orderCount);
                logger.info(message);
                return ResponseEntity.ok(ApiResponse.success(message, externalUrl));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure("외부 시스템으로 데이터 전송에 실패했습니다."));
            }

        } catch (OrderSyncException e) {
            logger.error("주문 전송 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("전송 실패: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("예상치 못한 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("시스템 오류: " + e.getMessage()));
        }
    }

    /**
     * 단일 주문을 외부 시스템에서 가져와 동기화합니다.
     *
     * @param externalUrl 외부 시스템 URL
     * @return 동기화 결과
     */
    @PostMapping("/sync-single")
    public ResponseEntity<ApiResponse<String>> syncSingleOrder(@RequestParam String externalUrl) {
        try {
            if (externalUrl == null || externalUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.failure("외부 시스템 URL이 필요합니다."));
            }

            orderSyncService.syncSingleOrder(externalUrl);
            String message = "단일 주문이 성공적으로 동기화되었습니다.";
            logger.info(message);
            return ResponseEntity.ok(ApiResponse.success(message, externalUrl));

        } catch (OrderSyncException e) {
            logger.error("단일 주문 동기화 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("동기화 실패: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("예상치 못한 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("시스템 오류: " + e.getMessage()));
        }
    }

    /**
     * 저장된 주문 수를 조회합니다.
     *
     * @return 주문 수
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getOrderCount() {
        try {
            int count = orderRepository.count();
            logger.info("주문 수 조회 완료: {}개", count);
            return ResponseEntity.ok(ApiResponse.success("주문 수 조회 완료", count));
        } catch (Exception e) {
            logger.error("주문 수 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("주문 수 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 모든 주문을 삭제합니다. (테스트 목적)
     *
     * @return 삭제 결과
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearAllOrders() {
        try {
            int beforeCount = orderRepository.count();
            orderRepository.clear();
            String message = String.format("%d개의 주문이 모두 삭제되었습니다.", beforeCount);
            logger.info(message);
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (Exception e) {
            logger.error("주문 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("주문 삭제 실패: " + e.getMessage()));
        }
    }
}