package com.orderSystem.connector;

import com.orderSystem.exception.DataConnectorException;

public class MockHttpDataConnector implements DataConnectorInterface {

    private String mockResponse;
    private boolean shouldFail = false;
    private String failureMessage = "Mock failure";

    public void setMockResponse(String response) {
        this.mockResponse = response;
        this.shouldFail = false;
    }

    public void setFailure(String message) {
        this.shouldFail = true;
        this.failureMessage = message;
    }

    @Override
    public String fetchData(String url) throws DataConnectorException {
        if (shouldFail) {
            throw new DataConnectorException(failureMessage);
        }
        return mockResponse;
    }

    @Override
    public boolean sendData(String url, String data) throws DataConnectorException {
        if (shouldFail) {
            throw new DataConnectorException(failureMessage);
        }
        return true;
    }
}