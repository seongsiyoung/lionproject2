package com.example.lionproject2backend.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portone")
public record PortOneProperties(
    String storeId,
    String apiSecret
) {

}
