package com.orderSystem.exception;

public class DataTransformException extends Exception {

    public DataTransformException(String message) {
        super(message);
    }

    public DataTransformException(String message, Throwable cause) {
        super(message, cause);
    }
}