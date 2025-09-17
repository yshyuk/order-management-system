package com.orderSystem.exception;


public class DataConnectorException extends Exception {

    public DataConnectorException(String message) {
        super(message);
    }

    public DataConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
