package com.orderSystem.exception;

public class OrderSyncException extends Exception {

    public OrderSyncException(String message) {
        super(message);
    }

    public OrderSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}