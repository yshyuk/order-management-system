package com.orderSystem.connector;

import com.orderSystem.exception.DataConnectorException;

public interface DataConnectorInterface {

    /**
     * 외부 시스템에서 데이터를 가져옵니다.
     *
     * @param url 외부 시스템 URL
     * @return JSON 형식의 데이터
     * @throws DataConnectorException 통신 오류 시 발생
     */
    String fetchData(String url) throws DataConnectorException;

    /**
     * 외부 시스템으로 데이터를 전송합니다.
     *
     * @param url  외부 시스템 URL
     * @param data JSON 형식의 데이터
     * @return 전송 성공 여부
     * @throws DataConnectorException 통신 오류 시 발생
     */
    boolean sendData(String url, String data) throws DataConnectorException;
}