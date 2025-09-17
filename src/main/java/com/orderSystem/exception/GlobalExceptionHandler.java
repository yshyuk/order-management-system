package com.orderSystem.exception;

import com.orderSystem.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        logger.warn("주문 조회 실패: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage(), 404);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataConnectorException.class)
    public ResponseEntity<ErrorResponse> handleDataConnectorException(DataConnectorException ex) {
        logger.error("데이터 연동 오류: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("DATA_CONNECTOR_ERROR", ex.getMessage(), 500);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataTransformException.class)
    public ResponseEntity<ErrorResponse> handleDataTransformException(DataTransformException ex) {
        logger.error("데이터 변환 오류: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("DATA_TRANSFORM_ERROR", ex.getMessage(), 400);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderSyncException.class)
    public ResponseEntity<ErrorResponse> handleOrderSyncException(OrderSyncException ex) {
        logger.error("주문 동기화 오류: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("ORDER_SYNC_ERROR", ex.getMessage(), 500);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex) {
        logger.warn("잘못된 인수: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("INVALID_ARGUMENT", ex.getMessage(), 400);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("예상치 못한 오류: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "시스템 내부 오류가 발생했습니다.", 500);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}