package com.orderSystem.config;

import com.orderSystem.connector.DataConnectorInterface;
import com.orderSystem.connector.HttpDataConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ConnectorConfig {

    @Bean
    @Primary
    public DataConnectorInterface dataConnector() {
        return new HttpDataConnector();
    }

    // 향후 다른 연결자 추가 가능
    // @Bean
    // @Qualifier("ftp")
    // public DataConnectorInterface ftpConnector() {
    //     return new FtpDataConnector();
    // }
}